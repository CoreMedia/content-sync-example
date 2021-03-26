package com.coremedia.blueprint.contentsync.client.xml;

import com.coremedia.blueprint.contentsync.client.IAPIConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * XML filter to collect outgoing references (link list, markup, struct, p13n rules).
 */
public class LinkCollectorHandler extends DefaultHandler {

  private final List<String> links = new ArrayList<>();

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (IAPIConstants.QNAME_A.equals(qName)) {
      addLink(IAPIConstants.ATTR_HREF, attributes);
    } else if (IAPIConstants.QNAME_SELECTION_RULES_CONTENT.equals(qName)) {
      addLink(IAPIConstants.ATTR_HREF, attributes);
    } else if (IAPIConstants.QNAME_LINK_PROPERTY.equals(qName)) {
      addLink(IAPIConstants.ATTR_HREF, attributes);
    } else if (IAPIConstants.QNAME_LINK_LIST_SINGLE_PROPERTY.equals(qName)) {
      addLink(IAPIConstants.ATTR_HREF, attributes);
    }

    super.startElement(uri, localName, qName, attributes);
  }

  private void addLink(String qname, Attributes attributes) {
    String link = attributes.getValue(qname);
    if (link != null && link.startsWith(IAPIConstants.ID_PREFIX)) {
      links.add(link);
    }
  }

  public List<String> getLinks() {
    return links;
  }
}
