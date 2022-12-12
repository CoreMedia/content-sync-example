package com.coremedia.blueprint.contentsync.client.model.property;

import com.coremedia.blueprint.contentsync.client.predicates.ContentTypePredicate;
import com.coremedia.blueprint.contentsync.client.xml.LinkCollectorHandler;
import com.coremedia.xml.MarkupFactory;

import java.util.Collections;
import java.util.List;

public class MarkupPropertyModel extends PropertyModel implements ReferenceProperty {

  public static final String TYPE_NAME = "Markup";


  private String value;


  public MarkupPropertyModel() {
    super(TYPE_NAME);
  }


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public List<String> getReferenceIDs() {
    if (getValue() == null) {
      return Collections.emptyList();
    }
    LinkCollectorHandler handler = new LinkCollectorHandler();
    MarkupFactory.fromString(getValue()).writeOn(handler);
    return handler.getLinks();
  }

  @Override
  public List<String> getFilteredReferences(ContentTypePredicate predicate) {
    return getReferenceIDs();
  }
}
