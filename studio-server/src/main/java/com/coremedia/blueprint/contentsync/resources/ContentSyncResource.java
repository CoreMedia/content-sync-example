package com.coremedia.blueprint.contentsync.resources;

import com.coremedia.blueprint.contentsync.ContentSyncProperties;
import com.coremedia.blueprint.contentsync.client.exception.IAPIInvalidResponseException;
import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.predicates.ContentPropertyNamePredicate;
import com.coremedia.blueprint.contentsync.client.predicates.ContentTypePredicate;
import com.coremedia.blueprint.contentsync.client.services.IAPIConnection;
import com.coremedia.blueprint.contentsync.client.services.IAPIRepository;
import com.coremedia.blueprint.contentsync.context.ContentSyncConnectionContextProvider;
import com.coremedia.blueprint.contentsync.model.ContentSyncModel;
import com.coremedia.blueprint.contentsync.model.ContentSyncReferenceModel;
import com.coremedia.blueprint.contentsync.model.ContentSyncWFSModel;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.workflow.Process;
import com.coremedia.cap.workflow.ProcessDefinition;
import com.coremedia.cap.workflow.WorkflowRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = ContentSyncResource.CONTENT_SYNC_RESOURCE_PREFIX + "/{" + ContentSyncResource.IDENTIFIER + "}", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentSyncResource {
  public static final String CONTENT_SYNC_RESOURCE_PREFIX = "contentsync";
  public static final String IDENTIFIER = "identifier";
  public static final String ID = "id";
  public static final String COREMEDIA_CAP_CONTENT = "coremedia:///cap/content/";
  public static final String REMOTE_SYNC_IDS = "remoteSyncIds";
  public static final String ENVIRONMENT = "environment";
  public static final String TOKEN = "token";
  public static final String SYNCTYPE = "synctype";
  public static final String CONTENT_TYPE_EXCLUSIONS = "contentTypeExcludes";
  public static final String PROPERTY_EXCLUDES = "propertyExcludes";
  public static final String RECURSION = "recursion";
  public static final String USE_V_2 = "useV2";

  private final ContentSyncConnectionContextProvider connectionContext;
  private final ContentRepository repository;
  private final Map<String, String> workflowNames;
  private final ContentSyncProperties properties;

  public ContentSyncResource(ContentRepository repository, ContentSyncProperties properties) {
    connectionContext = ContentSyncConnectionContextProvider.init(properties);
    this.repository = repository;
    this.properties = properties;
    workflowNames = properties.getSync2wfs();
  }

  @GetMapping("content/id/{id}")
  public ResponseEntity<ContentSyncModel> getId(@PathVariable(IDENTIFIER) String ident,
                                @PathVariable(ID) String id,
                                @RequestParam(value = CONTENT_TYPE_EXCLUSIONS,required = false) List<String> contentTypeExclusions) {
    ContentSyncModel contentSyncModel;
    try {
      ContentDataModel model = getConnectionFor(ident).getRepository().getContentById(id);
      contentSyncModel =  new ContentSyncModel(model, new ContentTypePredicate(contentTypeExclusions));
    } catch (IAPIInvalidResponseException exception){
      ContentDataModel model = new ContentDataModel();
      ContentDataModel modelMessage = new ContentDataModel();
      model.setId(COREMEDIA_CAP_CONTENT+"1");
      model.setPath("/");
      model.setType("Folder");
      model.setChildren(Collections.emptyList());
      contentSyncModel = new ContentSyncModel(model);
    }
    return new ResponseEntity<>(contentSyncModel,HttpStatus.OK);
  }

  @PostMapping("/startworkflow/{synctype}")
  public ContentSyncWFSModel startWorkflow(@PathVariable(IDENTIFIER) String ident,
                                           @PathVariable(SYNCTYPE) String type,
                                           @RequestBody Map<String, Object> body) {
    String workflowName = properties.getSync2wfs().get(type);
    ContentSyncWFSModel model = new ContentSyncWFSModel();
    if (workflowName != null) {
      model.setName(workflowName);
      WorkflowRepository wfsRepository = repository.getConnection().getWorkflowRepository();
      ProcessDefinition definition = wfsRepository.getProcessDefinition(workflowName);
      if (definition != null) {
        Process process = definition.create();
        process.set(REMOTE_SYNC_IDS, body.get(REMOTE_SYNC_IDS));
        process.set(ENVIRONMENT, properties.getHosts().get(ident));
        process.set(TOKEN, properties.getTokens().get(ident));
        process.set(USE_V_2,properties.getUseV2().getOrDefault(ident,false));
        process.start();
        model.setId(process.getId());
        model.setCreationDate(new GregorianCalendar().getTimeZone());
      }
    }
    return model;
  }

  @GetMapping("abort/{id}")
  public void abortWfs(@PathVariable(ID) String id){
    WorkflowRepository wfsRepository = repository.getConnection().getWorkflowRepository();
    Process process = wfsRepository.getProcess(id);
    if (process!=null){
      wfsRepository.getProcess(id).abort();
    }
  }

  @GetMapping("running")
  public List<ContentSyncWFSModel> getRunningWorkflows() {
    WorkflowRepository wfsRepository = repository.getConnection().getWorkflowRepository();
    return wfsRepository
            .getWorklistService()
            .getProcessesRunning()
            .stream()
            .filter(p -> workflowNames.containsValue(p.getType().getName()))
            .map(ContentSyncWFSModel::new)
            .collect(Collectors.toList());
  }

  @GetMapping("references/{id}/{recursion}")
  public ContentSyncReferenceModel references(@PathVariable(IDENTIFIER) String ident,
                                              @PathVariable(ID) String id,
                                              @PathVariable(RECURSION) int recursion,
                                              @RequestParam(value = CONTENT_TYPE_EXCLUSIONS,required = false) List<String> contentTypeExcludes,
                                              @RequestParam(value = PROPERTY_EXCLUDES,required = false) List<String> propertyExcludes) {
    IAPIRepository iapiRepository = getConnectionFor(ident)
            .getRepository();
    List<String> startReferences = iapiRepository
            .getContentById(id)
            .getReferences(new ContentPropertyNamePredicate(propertyExcludes),new ContentTypePredicate(contentTypeExcludes))
            .stream().map(r -> r.replaceAll(COREMEDIA_CAP_CONTENT, "")).collect(Collectors.toList());
    return new ContentSyncReferenceModel(iapiRepository, startReferences, recursion);
  }


  private IAPIConnection getConnectionFor(String ident) {
    return connectionContext.validateAndGet(repository, ident);
  }
}
