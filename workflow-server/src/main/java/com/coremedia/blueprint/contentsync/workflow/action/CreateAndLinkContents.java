package com.coremedia.blueprint.contentsync.workflow.action;

import com.coremedia.blueprint.contentsync.client.IAPIConstants;
import com.coremedia.blueprint.contentsync.client.IAPIContext;
import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel;
import com.coremedia.blueprint.contentsync.client.services.IAPIRepository;
import com.coremedia.blueprint.contentsync.workflow.exception.ContentTypeMismatchException;
import com.coremedia.blueprint.contentsync.workflow.property.PropertyMapper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.workflow.Process;
import com.coremedia.cap.workflow.Task;
import com.coremedia.workflow.common.util.SpringAwareLongAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This action creates local contents for a list of remote sync ids.
 * <p>
 * The mechanism is as follows: for each remote content which is to be synchronized
 * <ul>
 * <li>Determine whether corresponding local content exists for remote content.</li>
 * <li>Resolve all outgoing links in remote content (link lists, structs, markup, p13n markup) to either local
 * contents or other (to be synchronized) remote content.</li>
 * <li>If an outgoing link cannot be established (no local correspondent and not part of sync set),
 * remove link.</li>
 * <li>Convert properties of remote content to CoreMedia properties.</li>
 * <li>Create or update corresponding local content.</li>
 * </ul>
 * </p>
 * <p>
 * The operation can be optimized for cases of heavily-interlinked remote contents which have no local
 * counter-part yet: instead of creating content regardless of their inter-linking, a tree of link dependencies
 * between remote contents could be built and processed leaf-to-root. This way, re-processing of a remote content
 * with yet unsatisified links can be omitted. This approach has to take care of circular links within remote
 * content set, though.
 * </p>
 */
public class CreateAndLinkContents extends SpringAwareLongAction {

  private static final Logger LOG = LoggerFactory.getLogger(CreateAndLinkContents.class);

  private String environmentVariableName;
  private String tokenVariableName;
  private String remoteSyncIdsVariableName;

  public void setEnvironmentVariable(String environment) {
    this.environmentVariableName = environment;
  }

  public String getEnvironmentVariable() {
    return environmentVariableName;
  }

  public void setTokenVariable(String tokenVariableName) {
    this.tokenVariableName = tokenVariableName;
  }

  public String getTokenVariable() {
    return tokenVariableName;
  }

  public void setRemoteSyncIdsVariable(String remoteSyncIdsVariableName) {
    this.remoteSyncIdsVariableName = remoteSyncIdsVariableName;
  }

  public String getRemoteSyncIdsVariable() {
    return remoteSyncIdsVariableName;
  }

  @Override
  public final ActionParameters extractParameters(Task task) {
    Process process = task.getContainingProcess();
    return new ActionParameters(getNumericIds(process.getList(getRemoteSyncIdsVariable())),
            process.getString(getEnvironmentVariable()),
            process.getString(getTokenVariable())
            );
  }

  @Override
  protected Object doExecute(Object params) {

    @SuppressWarnings("unchecked" /* params is the return value of #extractParameters(Task) */)
    ActionParameters parameters = (ActionParameters) params;

    LOG.debug("starting sync with remote ids {} on environment {}", parameters.remoteSyncIds, parameters.environment);

    ContentRepository repository = getConnection().getContentRepository();

    // map from remote numeric content ids to remote contents
    Map<String, ContentDataModel> remoteContents = new HashMap<>();
    // map from remote numeric content ids to local numeric content ids
    Map<String, String> idMap = new HashMap<>();

    // 1st phase: process remote sync ids and store list of remote sync ids with references that can only be satisfied
    // with given or yet-to-be-sync'd local content in 2nd phase
    LOG.debug("sync 1st phase with ids {}", parameters.remoteSyncIds);
    List<String> remoteSyncIdsRedo = processRemoteIds(parameters.remoteSyncIds,
            remoteContents, idMap, repository, parameters.remoteRepository);

    // 2nd phase: re-process remote sync ids with (yet) unsatisfied links that can be resolved now with all remote
    // contents in place locally
    LOG.debug("sync 2nd phase with ids {}", remoteSyncIdsRedo);
    processRemoteIds(remoteSyncIdsRedo, remoteContents, idMap, repository, parameters.remoteRepository);

    // check in all (potentially) checked-out local contents
    for (String localId : idMap.values()) {
      Content localContent = repository.getContent(localId);
      if (localContent.isCheckedOut()) {
        localContent.checkIn();
      }
    }

    LOG.debug("finished sync");
    return null;
  }

  /**
   * Processes the given remote synd id list and returns the list of remote sync ids which need a second pass to
   * ultimately resolve references.
   *
   * @param remoteSyncIds  list of remote sync ids to create contents for locally.
   * @param remoteContents map from remote numeric content ids to remote contents for "caching". Will be extended by
   *                       method for future use.
   * @param idMap          map from remote numeric content ids to local numeric content ids. Will be extended by method for future use.
   * @return list of remote numeric content ids with references that can only be satisfied with given or
   * yet-to-be-sync'd local content in 2nd phase.
   */
  private List<String> processRemoteIds(List<String> remoteSyncIds,
                                        Map<String, ContentDataModel> remoteContents,
                                        Map<String, String> idMap,
                                        ContentRepository repository,
                                        IAPIRepository remoteRepository) {
    PropertyMapper propertyMapper = getPropertyMapper(repository, remoteRepository);
    List<String> remoteSyncIdsRedo = new ArrayList<>();

    for (String remoteId : remoteSyncIds) {
      LOG.debug("syncing remote id {}", remoteId);

      try {
        // get remote content
        ContentDataModel remoteContent = getRemoteContent(remoteId, remoteRepository, remoteContents);
        // get corresponding local content
        Content localContent = getLocalContent(remoteContent, repository, idMap);
        // resolve references
        resolveReferences(remoteContent, repository, remoteRepository, idMap, remoteSyncIds, remoteSyncIdsRedo);
        // get CoreMedia properties for remote content's properties
        Map<String, ?> properties = propertyMapper.getCoreMediaProperties(remoteContent, repository, idMap);
        // create or update local content
        createOrUpdateLocalContent(localContent, properties, remoteContent, repository, idMap);
      } catch (Exception e) {
        LOG.error("cannot sync remote content - skipping", e);
      }
    }
    return remoteSyncIdsRedo;
  }

  /**
   * Returns the remote content for the given id. Content will be taken from given map or from remote repository,
   * if not present there. Map will be extended after remote content was fetched from remote repository.
   */
  protected ContentDataModel getRemoteContent(String remoteId,
                                              IAPIRepository remoteRepository,
                                              Map<String, ContentDataModel> remoteContents) {
    // take remote content from map or retrieve via Ingest API
    ContentDataModel remoteContent = remoteContents.get(remoteId);
    if (remoteContent == null) {
      remoteContent = remoteRepository.getContentById(remoteId);
      // add to map of remote contents
      remoteContents.put(remoteId, remoteContent);
    }
    return remoteContent;
  }

  /**
   * Returns the local content for the given remote content. Content will be taken from given maps or from local content
   * repository, if not present there. Maps will be extended after local content was fetched from local content repository.
   */
  protected Content getLocalContent(ContentRefDataModel remoteContent,
                                    ContentRepository repository,
                                    Map<String, String> idMap) {
    // take local content from map or try to retrieve via UAPI
    String remoteId = remoteContent.getNumericId();
    Content localContent = repository.getChild(remoteContent.getPath());
    if (localContent != null) {
      if (!isCMTypesMatching(remoteContent, localContent)) {
        throw new ContentTypeMismatchException("content types for remote content " + remoteContent.getNumericId()
                + " do not match: remote content has type " + remoteContent.getType()
                + ", existing local content has type " + localContent.getType().getName());
      }
      putLocalContentOptional(localContent, remoteId, idMap);
    }
    return localContent;
  }

  protected void resolveReferences(ContentDataModel remoteContent,
                                   ContentRepository repository,
                                   IAPIRepository remoteRepository,
                                   Map<String, String> idMap,
                                   List<String> remoteSyncIds,
                                   List<String> remoteSyncIdsRedo) {
    // do two things:
    // (1) extend id mapping for remote references that can be satisfied with already existing local content
    // (2) extend list of remote sync ids with references that cannot be satisfied currently with local content
    //     but point to remote content that has not yet been sync'd locally (but will)
    List<String> remoteReferences = remoteContent.getReferences();
    for (String remoteReference : remoteReferences) {
      String remoteReferenceId = getNumericId(remoteReference);
      if (!idMap.containsKey(remoteReferenceId)) {
        // no mapping yet -> try to find local content counter-part
        ContentRefDataModel referencedRemoteContentRef = getReferencedRemoteContentRef(remoteReferenceId, remoteContent, remoteRepository);
        Content referencedLocalContent = repository.getChild(referencedRemoteContentRef.getPath());
        if (referencedLocalContent != null) {
          if (isCMTypesMatching(referencedRemoteContentRef, referencedLocalContent)) {
            putLocalContentOptional(referencedLocalContent, remoteReferenceId, idMap);
          } else {
            LOG.error("content type of local content on path {} ({}) does not match content type of remote content ({}) - skipping link",
                    referencedRemoteContentRef.getPath(), referencedLocalContent.getType().getName(), referencedRemoteContentRef.getPath());
          }
        } else if (remoteSyncIds.contains(remoteReferenceId)) {
          // no local content found but id is in sync set: add current content to redo list in order to update in 2nd phase
          if (!remoteSyncIdsRedo.contains(remoteContent.getNumericId())) {
            remoteSyncIdsRedo.add(remoteContent.getNumericId());
          }
        }
      }
    }
  }

  protected void createOrUpdateLocalContent(Content localContent,
                                            Map<String, ?> properties,
                                            ContentDataModel remoteContent,
                                            ContentRepository repository,
                                            Map<String, String> idMap) {
    if (localContent == null) {
      // create new local content, if it does not exist yet
      LOG.debug("creating local content on path {} for remote id {}", remoteContent.getPath(), remoteContent.getNumericId());
      localContent = repository.createChild(remoteContent.getPath(), remoteContent.getType(), properties);
      putLocalContentOptional(localContent, remoteContent.getNumericId(), idMap);
    } else {
      // update local content, if it does exist
      LOG.debug("setting properties on local content with id {} for remote content with id {}",
              localContent.getId(), remoteContent.getNumericId());
      if (!localContent.isCheckedOut()) {
        localContent.checkOut();
      }
      localContent.setProperties(properties);
    }

  }

  /**
   * Returns just the id of a CoreMedia content id (stripping prefix {@code coremedia:///cap/content/}).
   */
  private String getNumericId(String contentId) {
    return contentId.startsWith(IAPIConstants.ID_PREFIX) ? contentId.substring(IAPIConstants.ID_PREFIX.length()) : contentId;
  }

  /**
   * Returns just the numeric ids of CoreMedia content ids (stripping prefix {@code coremedia:///cap/content/}) and
   * filters non-string objects.
   */
  private List<String> getNumericIds(List<?> remoteSyncIds) {
    return remoteSyncIds.stream()
            .filter(id -> id instanceof String)
            .map(id -> getNumericId((String) id))
            .collect(Collectors.toList());
  }

  /**
   * Puts a local content, if not null, to the corresponding maps.
   *
   * @param content  the local content to store in maps.
   * @param remoteId the content's corresponding remote id (without prefix).
   * @param idMap    map from remote numeric content ids to local numeric content ids. Will be extended by method for future use.
   */
  private void putLocalContentOptional(Content content, String remoteId,
                                       Map<String, String> idMap) {
    if (content != null) {
      // content present, put to corresponding maps for later reference
      String id = getNumericId(content.getId());
      idMap.put(remoteId, getNumericId(id));
    }
  }

  /**
   * Returns the reference model for the given remote reference id or {@code null}, if no remote content with this id can be retrieved.
   * For determination it uses information already existing in the given
   * remote content (via {@link com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel}s used
   * as reference in link lists). If no such information can be found, the remote repository is queried (necessary
   * for references in markup and structs).
   */
  private ContentRefDataModel getReferencedRemoteContentRef(String referenceId, ContentDataModel remoteContent, IAPIRepository remoteRepository) {
    // firstly, check references in link list properties - paths are given there right away
    for (ContentRefDataModel referenceModel : remoteContent.getReferenceModels()) {
      if (referenceModel.getNumericId().equals(referenceId)) {
        return referenceModel;
      }
    }
    // secondly, get referenced remote content to determine path
    try {
      return remoteRepository.getContentById(referenceId);
    } catch (Exception e) {
      LOG.error("cannot resolve reference for remote id " + referenceId, e);
      return null;
    }
  }

  /**
   * Returns a property mapper for the given content repository. {@code protected} for testing purposes.
   */
  protected PropertyMapper getPropertyMapper(ContentRepository repository, IAPIRepository iapiRepository) {
    return new PropertyMapper(repository, iapiRepository);
  }

  /**
   * Returns {@code true}, if remote and local contents's types match.
   */
  private boolean isCMTypesMatching(ContentRefDataModel remoteContent, Content localContent) {
    return remoteContent.getType().equals(localContent.getType().getName());
  }

  static final class ActionParameters {

    List<String> remoteSyncIds;
    String environment;
    String token;
    IAPIRepository remoteRepository;

    ActionParameters(List<String> remoteSyncIds,
                     String environment,
                     String token) {
      this.remoteSyncIds = remoteSyncIds;
      this.environment = environment;
      this.token = token;

      remoteRepository = IAPIContext
              .withContext(new ContentSyncConnectionContext(
                      environment,
                      token,
                      null,
                      null)
              )
              .build()
              .getRepository();
    }
  }
}
