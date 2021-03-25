package com.coremedia.blueprint.contentsync.client.model.property;

import com.coremedia.blueprint.contentsync.client.predicates.ContentTypePredicate;

import java.util.List;
import java.util.function.Predicate;

public interface ReferenceProperty {

  /**
   * Returns the CoreMedia content IDs
   * ({@link com.coremedia.blueprint.contentsync.client.IAPIConstants#ID_PREFIX coremedia:///cap/content/...})
   * referenced by the property.
   */
  List<String> getReferenceIDs();

  List<String> getFilteredReferences(ContentTypePredicate predicate);
}
