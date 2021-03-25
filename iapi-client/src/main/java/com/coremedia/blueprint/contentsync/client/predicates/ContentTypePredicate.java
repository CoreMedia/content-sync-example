package com.coremedia.blueprint.contentsync.client.predicates;

import java.util.List;
import java.util.function.Predicate;

public class ContentTypePredicate implements Predicate<String> {

  private List<String> contentTypeNames;

  public ContentTypePredicate(List<String> contentTypeNames) {
    this.contentTypeNames = contentTypeNames;
  }

  @Override
  public boolean test(String s) {
    return !contentTypeNames.stream().anyMatch(p->p.equals(s));
  }
}
