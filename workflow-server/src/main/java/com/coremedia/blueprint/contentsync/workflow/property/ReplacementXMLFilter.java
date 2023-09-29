package com.coremedia.blueprint.contentsync.workflow.property;

import com.coremedia.blueprint.contentsync.client.IAPIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.Collections;
import java.util.Map;

/**
 * XML filter to map content references according to a given map of id replacements. References that cannot be
 * replaced are removed. Removal is specific to the type of reference (link list, markup, struct, p13n rules).
 */
public class ReplacementXMLFilter extends XMLFilterImpl {

  private static final Logger LOG = LoggerFactory.getLogger(ReplacementXMLFilter.class);

  private Map<String, String> replacements;
  protected boolean deleteEntity = false;

  public ReplacementXMLFilter(Map<String, String> replacements) {
    super();
    this.replacements = Collections.emptyMap();
    if (null != replacements) {
      this.replacements = replacements;
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (IAPIConstants.QNAME_A.equals(qName)
            || IAPIConstants.QNAME_LINK_PROPERTY.equals(qName)
            || IAPIConstants.QNAME_LINK_LIST_SINGLE_PROPERTY.equals(qName)
            || IAPIConstants.QNAME_SELECTION_RULES_CONTENT.equals(qName)) {
      deleteEntity = replaceLink(qName, atts);
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

  private boolean replaceLink(String qName, Attributes attributes) {
    String link = attributes.getValue(IAPIConstants.ATTR_HREF);
    if (link != null && link.startsWith(IAPIConstants.ID_PREFIX)) {
      String id = link.substring(IAPIConstants.ID_PREFIX.length());
      if (replacements != null && replacements.containsKey(id)) {
        String replacement = replacements.get(id);
        ((AttributesImpl) attributes).setValue(attributes.getIndex(IAPIConstants.ATTR_HREF), IAPIConstants.ID_PREFIX + replacement);
        return false;
      } else {
        // if no replacement could be made, remove link either by replacing with some default or by signalling
        // removal of the element altogether
        switch (qName) {
          case IAPIConstants.QNAME_A:
            ((AttributesImpl) attributes).setValue(attributes.getIndex(IAPIConstants.ATTR_HREF), "#");
            LOG.debug("removing unresolvable rich text link target {}", link);
            break;
          case IAPIConstants.QNAME_SELECTION_RULES_CONTENT:
            ((AttributesImpl) attributes).setValue(attributes.getIndex(IAPIConstants.ATTR_HREF), "");
            LOG.debug("removing unresolvable p13n selection rules link target {}", link);
            break;
          case IAPIConstants.QNAME_LINK_PROPERTY:
            ((AttributesImpl) attributes).removeAttribute(attributes.getIndex(IAPIConstants.ATTR_HREF));
            LOG.debug("removing unresolvable settings link property target {}", link);
            break;
          default:
            LOG.debug("removing unresolvable link target {} on xml element <{}>", link, qName);
            return true;
        }
      }
    }
    return false;
  }
}
