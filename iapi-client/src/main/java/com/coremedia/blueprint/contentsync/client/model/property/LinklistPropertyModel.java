package com.coremedia.blueprint.contentsync.client.model.property;

import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel;
import com.coremedia.blueprint.contentsync.client.predicates.ContentTypePredicate;

import java.util.List;
import java.util.stream.Collectors;

public class LinklistPropertyModel extends PropertyModel implements ReferenceProperty {

  public static final String TYPE_NAME = "Linklist";


  private List<ContentRefDataModel> references;


  public LinklistPropertyModel() {
    super(TYPE_NAME);
  }


  public List<ContentRefDataModel> getReferences() {
    return references;
  }

  public void setReferences(List<ContentRefDataModel> references) {
    this.references = references;
  }

  @Override
  public List<String> getReferenceIDs() {
    return getReferences().stream()
            .map(ContentRefDataModel::getId)
            .collect(Collectors.toList());
  }

  @Override
  public List<String> getFilteredReferences(ContentTypePredicate predicate) {
    return getReferences()
            .stream()
            .filter(r -> predicate.test(r.getType()))
            .map(ContentRefDataModel::getId)
            .collect(Collectors.toList());
  }
}
