package com.coremedia.blueprint.contentsync.workflow.property;

import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel;
import com.coremedia.blueprint.contentsync.client.model.property.*;
import com.coremedia.blueprint.contentsync.client.services.IAPIRepository;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.BlobService;
import com.coremedia.cap.common.UrlBlob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PropertyMapperTest {

  private static final String STRUCT_ORIGINAL_STRING = "<Struct xmlns=\"http://www.coremedia.com/2008/struct\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
          "<LinkListProperty Name=\"items\" LinkType=\"coremedia:///cap/contenttype/CMTeasable\">" +
          "<Link xlink:href=\"coremedia:///cap/content/2\"/>" +
          "<Link xlink:href=\"coremedia:///cap/content/10\"/>" +
          "</LinkListProperty>" +
          "<LinkProperty Name=\"section\" LinkType=\"coremedia:///cap/contenttype/CMSymbol\" xlink:href=\"coremedia:///cap/content/4\"/>" +
          "<LinkProperty Name=\"section\" LinkType=\"coremedia:///cap/contenttype/CMSymbol\" xlink:href=\"coremedia:///cap/content/10\"/>" +
          "</Struct>";
  private static final String STRUCT_REPLACED_STRING = "<Struct xmlns=\"http://www.coremedia.com/2008/struct\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
          "<LinkListProperty Name=\"items\" LinkType=\"coremedia:///cap/contenttype/CMTeasable\">" +
          "<Link xlink:href=\"coremedia:///cap/content/6\"/>" +
          "</LinkListProperty>" +
          "<LinkProperty Name=\"section\" LinkType=\"coremedia:///cap/contenttype/CMSymbol\" xlink:href=\"coremedia:///cap/content/8\"/>" +
          "<LinkProperty Name=\"section\" LinkType=\"coremedia:///cap/contenttype/CMSymbol\"/>" +
          "</Struct>";
  private static final Markup STRUCT_MARKUP = MarkupFactory.fromString(STRUCT_REPLACED_STRING);
  private static final Struct STRUCT_REPLACED = mock(Struct.class);
  private static final Content CONTENT_6 = mock(Content.class);
  private static final Content CONTENT_8 = mock(Content.class);
  private static final String RICHTEXT_ORIGINAL_STRING = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
          "<p><a xlink:show=\"embed\" xlink:href=\"coremedia:///cap/content/2\" xlink:type=\"simple\">link</a></p>" +
          "<p><a xlink:show=\"embed\" xlink:href=\"coremedia:///cap/content/10\" xlink:type=\"simple\">link</a></p>" +
          "<p><a xlink:show=\"embed\" xlink:href=\"https://www.coremedia.com\" xlink:type=\"simple\">link</a></p>" +
          "</div>";
  private static final String RICHTEXT_REPLACED_STRING = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
          "<p><a xlink:show=\"embed\" xlink:href=\"coremedia:///cap/content/6\" xlink:type=\"simple\">link</a></p>" +
          "<p><a xlink:show=\"embed\" xlink:href=\"#\" xlink:type=\"simple\">link</a></p>" +
          "<p><a xlink:show=\"embed\" xlink:href=\"https://www.coremedia.com\" xlink:type=\"simple\">link</a></p>" +
          "</div>";
  private static final UrlBlob BLOB = mock(UrlBlob.class);
  private static final byte[] BLOB_BYTES = new byte[]{0x0};

  private static final BlobService BLOB_SERVICE = mock(BlobService.class);

  private PropertyMapper testling;

  private Map<String, String> replacements;
  private Map<String, Content> contents;

  private final ContentRepository repository = mock(ContentRepository.class, RETURNS_DEEP_STUBS);
  private final IAPIRepository remoteRepository = mock(IAPIRepository.class);

  @BeforeEach
  public void setUp() throws Exception {
    testling = new PropertyMapper(repository, remoteRepository);
    replacements = new HashMap<>();
    replacements.put("2", "6");
    replacements.put("4", "8");
    contents = new HashMap<>();
    contents.put("6", CONTENT_6);
    contents.put("8", CONTENT_8);
    when(repository.getConnection().getStructService().fromMarkup(eq(STRUCT_MARKUP))).thenReturn(STRUCT_REPLACED);
    when(repository.getConnection().getBlobService()).thenReturn(BLOB_SERVICE);
    when(remoteRepository.getBlob("data", "2")).thenReturn(BLOB_BYTES);
    when(BLOB_SERVICE.fromBytes(BLOB_BYTES, "image/jpeg")).thenReturn(BLOB);
    when(STRUCT_REPLACED.toMarkup()).thenReturn(STRUCT_MARKUP);
  }

  @Test
  public void test_getCoreMediaProperties() {
    Map<String, PropertyModel> properties = new HashMap<>();
    properties.put("string", getStringPropertyModel());
    properties.put("integer", getIntegerPropertyModel());
    properties.put("date", getDatePropertyModel());
    properties.put("linklist", getLinklistPropertyModel());
    properties.put("struct", getStructPropertyModel());
    properties.put("markup", getMarkupPropertyModel());
    properties.put("blob", getBlobPorpertyModel());
    ContentDataModel model = new ContentDataModel();
    model.setProperties(properties);

    Map<String, Object> coreMediaProperties = testling.getCoreMediaProperties(model, replacements, contents);
    assertEquals(7, coreMediaProperties.size());
    assertEquals("stringPropertyModelValue", coreMediaProperties.get("string"));
    assertEquals(4711, coreMediaProperties.get("integer"));
    assertEquals(GregorianCalendar.from(ZonedDateTime.of(1969, 12, 10, 8, 40, 0, 0, ZoneId.of("CET"))), coreMediaProperties.get("date"));
    assertTrue(coreMediaProperties.get("linklist") instanceof List);
    assertEquals(Arrays.asList(CONTENT_6, CONTENT_8), coreMediaProperties.get("linklist"));
    assertTrue(coreMediaProperties.get("struct") instanceof Struct);
    assertEquals(STRUCT_REPLACED_STRING, ((Struct) coreMediaProperties.get("struct")).toMarkup().toString());
    assertTrue(coreMediaProperties.get("markup") instanceof Markup);
    assertEquals(RICHTEXT_REPLACED_STRING, coreMediaProperties.get("markup").toString());
    assertTrue(coreMediaProperties.get("blob") instanceof Blob);
    assertEquals(BLOB, coreMediaProperties.get("blob"));
  }

  private StringPropertyModel getStringPropertyModel() {
    StringPropertyModel model = new StringPropertyModel();
    model.setValue("stringPropertyModelValue");
    return model;
  }

  private IntegerPropertyModel getIntegerPropertyModel() {
    IntegerPropertyModel model = new IntegerPropertyModel();
    model.setValue(4711);
    return model;
  }

  private DatePropertyModel getDatePropertyModel() {
    DatePropertyModel model = new DatePropertyModel();
    model.setValue(ZonedDateTime.of(1969, 12, 10, 8, 40, 0, 0, ZoneId.of("CET")));
    return model;
  }

  private LinklistPropertyModel getLinklistPropertyModel() {
    LinklistPropertyModel model = new LinklistPropertyModel();
    model.setReferences(Arrays.asList(getReference("2"), getReference("4"), getReference("99")));
    return model;
  }

  private ContentRefDataModel getReference(String id) {
    ContentRefDataModel refDataModel = new ContentRefDataModel();
    refDataModel.setId("coremedia:///cap/content/" + id);
    return refDataModel;
  }

  private StructPropertyModel getStructPropertyModel() {
    StructPropertyModel model = new StructPropertyModel();
    model.setValue(STRUCT_ORIGINAL_STRING);
    return model;
  }

  private MarkupPropertyModel getMarkupPropertyModel() {
    MarkupPropertyModel model = new MarkupPropertyModel();
    model.setValue(RICHTEXT_ORIGINAL_STRING);
    return model;
  }

  private ServerBlobPropertyModel getBlobPorpertyModel() {
    ServerBlobPropertyModel model = new ServerBlobPropertyModel();
    model.setMimeType("image/jpeg");
    model.setSize(4711);
    model.setUrl("https://ingest.host/path/2/properties/data/data");
    return model;
  }
}
