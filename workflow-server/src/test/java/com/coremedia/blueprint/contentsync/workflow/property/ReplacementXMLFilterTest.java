package com.coremedia.blueprint.contentsync.workflow.property;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class ReplacementXMLFilterTest {

  private ReplacementXMLFilter testling;

  private final AttributesImpl attributes = mock(AttributesImpl.class);

  @BeforeEach
  public void setUp() {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("1", "2");
    testling = new ReplacementXMLFilter(replacements);
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/1");
    when(attributes.getIndex("xlink:href")).thenReturn(0);
  }

  @Test
  public void test_replaceAnchor() throws SAXException {
    testling.startElement("uri", "a", "a", attributes);
    verify(attributes, times(1)).setValue(0, "coremedia:///cap/content/2");
  }

  @Test
  public void test_replaceLink() throws SAXException {
    testling.startElement("uri", "Link", "Link", attributes);
    verify(attributes, times(1)).setValue(0, "coremedia:///cap/content/2");
  }

  @Test
  public void test_replaceLinkProperty() throws SAXException {
    testling.startElement("uri", "LinkProperty", "LinkProperty", attributes);
    verify(attributes, times(1)).setValue(0, "coremedia:///cap/content/2");
  }
}