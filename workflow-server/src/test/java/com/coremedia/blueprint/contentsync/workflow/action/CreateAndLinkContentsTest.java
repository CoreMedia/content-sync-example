package com.coremedia.blueprint.contentsync.workflow.action;

import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.property.LinklistPropertyModel;
import com.coremedia.blueprint.contentsync.client.model.property.MarkupPropertyModel;
import com.coremedia.blueprint.contentsync.client.model.property.PropertyModel;
import com.coremedia.blueprint.contentsync.client.model.property.StructPropertyModel;
import com.coremedia.blueprint.contentsync.client.services.IAPIRepository;
import com.coremedia.blueprint.contentsync.workflow.property.PropertyMapper;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.workflow.common.util.SpringContextManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateAndLinkContentsTest {

  private static final CapConnection CONNECTION = mock(CapConnection.class, RETURNS_DEEP_STUBS);
  private static final Content LOCAL_CONTENT_106 = mock(Content.class);
  private static final Content LOCAL_CONTENT_112 = mock(Content.class);
  private static final Content LOCAL_CONTENT_118 = mock(Content.class);
  private static final Content LOCAL_CONTENT_202 = mock(Content.class);

  private static final IAPIRepository REMOTE_REPOSITORY = mock(IAPIRepository.class);

  private static final String STRUCT = "<Struct xmlns=\"http://www.coremedia.com/2008/struct\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
          "<LinkListProperty Name=\"items\" LinkType=\"coremedia:///cap/contenttype/CMTeasable\">" +
          "<Link xlink:href=\"coremedia:///cap/content/10\"/>" +
          "<Link xlink:href=\"coremedia:///cap/content/12\"/>" +
          "<Link xlink:href=\"coremedia:///cap/content/14\"/>" +
          "</LinkListProperty>" +
          "<LinkProperty Name=\"l1\" LinkType=\"coremedia:///cap/contenttype/CMSymbol\" xlink:href=\"coremedia:///cap/content/10\"/>" +
          "<LinkProperty Name=\"l2\" LinkType=\"coremedia:///cap/contenttype/CMSymbol\" xlink:href=\"coremedia:///cap/content/12\"/>" +
          "<LinkProperty Name=\"l3\" LinkType=\"coremedia:///cap/contenttype/CMSymbol\" xlink:href=\"coremedia:///cap/content/14\"/>" +
          "</Struct>";
  private static final String MARKUP = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
          "<p><a xlink:show=\"embed\" xlink:href=\"coremedia:///cap/content/16\" xlink:type=\"simple\">link</a></p>" +
          "<p><a xlink:show=\"embed\" xlink:href=\"coremedia:///cap/content/18\" xlink:type=\"simple\">link</a></p>" +
          "<p><a xlink:show=\"embed\" xlink:href=\"coremedia:///cap/content/20\" xlink:type=\"simple\">link</a></p>" +
          "</div>";

  private static final PropertyMapper PROPERTY_MAPPER = mock(PropertyMapper.class);

  private CreateAndLinkContents testling;

  private ContentRepository contentRepository = mock(ContentRepository.class);

  private ContentDataModel remoteContent2;
  private ContentDataModel remoteContent4;
  private ContentDataModel remoteContent6;
  private ContentDataModel remoteContent8;
  private ContentDataModel remoteContent10;
  private ContentDataModel remoteContent12;
  private ContentDataModel remoteContent14;
  private ContentDataModel remoteContent16;
  private ContentDataModel remoteContent18;
  private ContentDataModel remoteContent20;


  @BeforeEach
  public void setUp() {
    when(CONNECTION.getContentRepository()).thenReturn(contentRepository);
    when(CONNECTION.getWorkflowRepository().getManagerService().getManager(SpringContextManager.NAME)).thenReturn(mock(SpringContextManager.class));
    when(contentRepository.getChild("path6")).thenReturn(LOCAL_CONTENT_106);
    when(contentRepository.getChild("path12")).thenReturn(LOCAL_CONTENT_112);
    when(contentRepository.getChild("path18")).thenReturn(LOCAL_CONTENT_118);
    when(contentRepository.createChild(eq("path2"), eq("CMTeaser"), any())).thenReturn(LOCAL_CONTENT_202);
    when(LOCAL_CONTENT_106.getId()).thenReturn("coremedia:///cap/content/106");
    when(LOCAL_CONTENT_112.getId()).thenReturn("coremedia:///cap/content/112");
    when(LOCAL_CONTENT_118.getId()).thenReturn("coremedia:///cap/content/118");
    when(LOCAL_CONTENT_202.getId()).thenReturn("coremedia:///cap/content/202");

    Map<String, PropertyModel> properties = new HashMap<>();
    LinklistPropertyModel linklist = new LinklistPropertyModel();
    linklist.setReferences(Arrays.asList(
            createRemoteContent("coremedia:///cap/content/4", "path4"),
            createRemoteContent("coremedia:///cap/content/6", "path6"),
            createRemoteContent("coremedia:///cap/content/8", "path8")));
    properties.put("linklist", linklist);
    StructPropertyModel struct = new StructPropertyModel();
    struct.setValue(STRUCT);
    properties.put("struct", struct);
    MarkupPropertyModel markup = new MarkupPropertyModel();
    markup.setValue(MARKUP);
    properties.put("markup", markup);
    remoteContent2 = createRemoteContent("coremedia:///cap/content/2", "path2");
    remoteContent2.setProperties(properties);
    remoteContent4 = createRemoteContent("coremedia:///cap/content/4", "path4");
    remoteContent6 = createRemoteContent("coremedia:///cap/content/6", "path6");
    remoteContent8 = createRemoteContent("coremedia:///cap/content/8", "path8");
    remoteContent10 = createRemoteContent("coremedia:///cap/content/10", "path10");
    remoteContent12 = createRemoteContent("coremedia:///cap/content/12", "path12");
    remoteContent14 = createRemoteContent("coremedia:///cap/content/14", "path14");
    remoteContent16 = createRemoteContent("coremedia:///cap/content/16", "path16");
    remoteContent18 = createRemoteContent("coremedia:///cap/content/18", "path18");
    remoteContent20 = createRemoteContent("coremedia:///cap/content/20", "path20");

    when(REMOTE_REPOSITORY.getContentById(any())).thenReturn(remoteContent2);
    when(REMOTE_REPOSITORY.getContentById("4")).thenReturn(remoteContent4);
    when(REMOTE_REPOSITORY.getContentById("6")).thenReturn(remoteContent6);
    when(REMOTE_REPOSITORY.getContentById("8")).thenReturn(remoteContent8);
    when(REMOTE_REPOSITORY.getContentById("10")).thenReturn(remoteContent10);
    when(REMOTE_REPOSITORY.getContentById("12")).thenReturn(remoteContent12);
    when(REMOTE_REPOSITORY.getContentById("14")).thenReturn(remoteContent14);
    when(REMOTE_REPOSITORY.getContentById("16")).thenReturn(remoteContent16);
    when(REMOTE_REPOSITORY.getContentById("18")).thenReturn(remoteContent18);
    when(REMOTE_REPOSITORY.getContentById("20")).thenReturn(remoteContent20);

    // TODO set up PROPERTY_MAPPER for link list, struct, and markup
    //when(PROPERTY_MAPPER.getCoreMediaProperties(remoteContent2, eq()))

    testling = new CreateAndLinkContents() {
      protected PropertyMapper getPropertyMapper(ContentRepository repository) {
        return PROPERTY_MAPPER;
      }
    };
    testling.setConnection(CONNECTION);
  }

  @Test
  public void test_getRemoteContent() {
    Map<String, ContentDataModel> remoteContents = new HashMap<>();
    ContentDataModel remoteContent = testling.getRemoteContent("2", REMOTE_REPOSITORY, remoteContents);
    assertEquals(remoteContent2, remoteContent);
    remoteContent = testling.getRemoteContent("2", REMOTE_REPOSITORY, remoteContents);
    assertEquals(remoteContent2, remoteContent);
    assertEquals(1, remoteContents.size());
    assertEquals(remoteContent2, remoteContents.get("2"));
    verify(REMOTE_REPOSITORY, times(1)).getContentById("2");
  }

  @Test
  public void test_getLocalContent() {
    Map<String, String> idMap = new HashMap<>();
    Map<String, Content> localContents = new HashMap<>();
    Content localContent = testling.getLocalContent(remoteContent2, contentRepository, idMap, localContents);
    assertNull(localContent);
    localContent = testling.getLocalContent(remoteContent2, contentRepository, idMap, localContents);
    assertNull(localContent);
    localContent = testling.getLocalContent(remoteContent6, contentRepository, idMap, localContents);
    assertEquals(LOCAL_CONTENT_106, localContent);
    assertEquals(1, idMap.size());
    assertEquals("106", idMap.get("6"));
    assertEquals(1, localContents.size());
    assertEquals(LOCAL_CONTENT_106, localContents.get("106"));
    verify(contentRepository, times(2)).getChild("path2");
    verify(contentRepository, times(1)).getChild("path6");
  }

  @Test
  public void test_resolveReferences() {
    Map<String, String> idMap = new HashMap<>();
    Map<String, Content> localContents = new HashMap<>();
    List<String> remoteSyncIds = Arrays.asList("2", "4", "10", "16");
    List<String> remoteSyncIdsRedo = new ArrayList<>();
    testling.resolveReferences(remoteContent2, contentRepository, REMOTE_REPOSITORY, idMap, localContents, remoteSyncIds, remoteSyncIdsRedo);
    assertEquals(3, idMap.size());
    assertMapEquals(idMap, "6", "106", "12", "112", "18", "118");
    assertEquals(3, localContents.size());
    assertMapEquals(localContents, "106", LOCAL_CONTENT_106, "112", LOCAL_CONTENT_112, "118", LOCAL_CONTENT_118);
    assertEquals(1, remoteSyncIdsRedo.size());
    assertEquals("2", remoteSyncIdsRedo.get(0));
  }

  @Test
  public void test_createOrUpdateLocalContent_create() {
    Map<String, String> idMap = new HashMap<>();
    Map<String, Content> localContents = new HashMap<>();
    testling.createOrUpdateLocalContent(null, new HashMap<>(), remoteContent2, contentRepository, idMap, localContents);
    verify(contentRepository, times(1)).createChild(eq("path2"), eq("CMTeaser"), any());
    assertEquals(1, idMap.size());
    assertEquals("202", idMap.get("2"));
    assertEquals(1, localContents.size());
    assertEquals(LOCAL_CONTENT_202, localContents.get("202"));
  }

  @Test
  public void test_createOrUpdateLocalContent_update() {
    Map<String, String> idMap = new HashMap<>();
    Map<String, Content> localContents = new HashMap<>();
    testling.createOrUpdateLocalContent(LOCAL_CONTENT_106, new HashMap<>(), remoteContent6, contentRepository, idMap, localContents);
    verify(contentRepository, never()).createChild(anyString(), anyString(), any());
    verify(LOCAL_CONTENT_106, times(1)).setProperties(any());
    assertEquals(0, idMap.size());
    assertEquals(0, localContents.size());
  }

  private void assertMapEquals(Map<?, ?> actual, Object... expected) {
    Iterator<?> it = Arrays.stream(expected).iterator();
    while (it.hasNext()) {
      Object key = it.next();
      Object value = it.next();
      assertEquals(value, actual.get(key));
    }
  }

/*
  CreateAndLinkContents.ActionParameters params = new CreateAndLinkContents.ActionParameters(
          Arrays.asList("2", "4", "10", "16"),
          "environment",
          "token",
          REMOTE_REPOSITORY
  );
*/

  private ContentDataModel createRemoteContent(String id, String path) {
    ContentDataModel remoteContent = new ContentDataModel();
    remoteContent.setPath(path);
    remoteContent.setId(id);
    remoteContent.setType("CMTeaser");
    return remoteContent;
  }
}
