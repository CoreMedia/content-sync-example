package com.coremedia.blueprint.contentsync.workflow.property;

import com.coremedia.blueprint.contentsync.client.IAPIConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.Collections;
import java.util.Map;

public class ReplacementXMLFilter extends XMLFilterImpl {

  private Map<String, String> replacements;
  private boolean deleteEntity = false;

  public ReplacementXMLFilter(Map<String, String> replacements) {
    super();
    if (replacements == null) {
      this.replacements = Collections.emptyMap();
    }
    this.replacements = replacements;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (IAPIConstants.QNAME_A.equals(qName)) {
      deleteEntity = replaceLink(IAPIConstants.QNAME_A, atts);
    } else if (IAPIConstants.QNAME_LINK_PROPERTY.equals(qName)) {
      deleteEntity = replaceLink(IAPIConstants.QNAME_LINK_PROPERTY, atts);
    } else if (IAPIConstants.QNAME_LINK_LIST_SINGLE_PROPERTY.equals(qName)) {
      deleteEntity = replaceLink(IAPIConstants.QNAME_LINK_LIST_SINGLE_PROPERTY, atts);
    }
    if (!deleteEntity) {
      super.startElement(uri, localName, qName, atts);
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (!deleteEntity) {
      super.characters(ch, start, length);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (!deleteEntity) {
      super.endElement(uri, localName, qName);
    }
    deleteEntity = false;
  }

  private boolean replaceLink(String qname, Attributes attributes) {
    String link = attributes.getValue(IAPIConstants.ATTR_HREF);
    if (link!=null && link.startsWith(IAPIConstants.ID_PREFIX)) {
      String id = link.substring(IAPIConstants.ID_PREFIX.length());
      if (replacements != null && replacements.containsKey(id)) {
        String replacement = replacements.get(id);
        ((AttributesImpl) attributes).setValue(attributes.getIndex(IAPIConstants.ATTR_HREF), IAPIConstants.ID_PREFIX + replacement);
        return false;
      } else {
        return true;
      }
    }
    return false;
  }
}
