package com.coremedia.blueprint.contentsync.client.xml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LinkCollectorHandlerTest {

  private LinkCollectorHandler testling;

  private final AttributesImpl attributes = mock(AttributesImpl.class);

  @BeforeEach
  public void setUp() {
    testling = new LinkCollectorHandler();
    when(attributes.getIndex("xlink:href")).thenReturn(0);
  }

  @Test
  public void test_collectAnchors() throws SAXException {
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/1");
    testling.startElement("uri", "a", "a", attributes);
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/3");
    testling.startElement("uri", "a", "a", attributes);
    assertEquals(Arrays.asList("coremedia:///cap/content/1", "coremedia:///cap/content/3"), testling.getLinks());
  }

  @Test
  public void test_collectLinks() throws SAXException {
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/1");
    testling.startElement("uri", "Link", "Link", attributes);
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/3");
    testling.startElement("uri", "Link", "Link", attributes);
    assertEquals(Arrays.asList("coremedia:///cap/content/1", "coremedia:///cap/content/3"), testling.getLinks());
  }

  @Test
  public void test_collectLinkProperties() throws SAXException {
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/1");
    testling.startElement("uri", "LinkProperty", "LinkProperty", attributes);
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/3");
    testling.startElement("uri", "LinkProperty", "LinkProperty", attributes);
    assertEquals(Arrays.asList("coremedia:///cap/content/1", "coremedia:///cap/content/3"), testling.getLinks());
  }

  @Test
  public void test_collectSelectionRulesProperties() throws SAXException {
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/1");
    testling.startElement("uri", "content", "content", attributes);
    when(attributes.getValue("xlink:href")).thenReturn("coremedia:///cap/content/3");
    testling.startElement("uri", "content", "content", attributes);
    assertEquals(Arrays.asList("coremedia:///cap/content/1", "coremedia:///cap/content/3"), testling.getLinks());
  }
}
