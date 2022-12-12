package com.coremedia.blueprint.contentsync.client.predicates;


import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.function.Predicate;

@DefaultAnnotation(NonNull.class)
public class ContentPropertyNamePredicate implements Predicate<String> {
  private List<String> propertyNames;

  public ContentPropertyNamePredicate(List<String> contentPropertyNames) {
    propertyNames = contentPropertyNames;
  }

  @Override
  public boolean test(String origProperty) {
    return !propertyNames.stream().anyMatch(p -> p.equals(origProperty));
  }
}
