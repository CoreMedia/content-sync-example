package com.coremedia.blueprint.contentsync.model;

import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.services.IAPIRepository;

import java.util.ArrayList;
import java.util.List;

public class ContentSyncReferenceModel {
  public static final String COREMEDIA_CAP_CONTENT = "coremedia:///cap/content/";
  private List<String> references = new ArrayList<>();
  private IAPIRepository repository;

  public ContentSyncReferenceModel(IAPIRepository iapiRepository, List<String> initialIdList, int recursion) {
    this.repository = iapiRepository;
    references.addAll(initialIdList);
    handleSubReferences(references, 1, recursion);
  }

  private void handleSubReferences(List<String> references, int currentRecursion, int maxRecursion) {
    if (currentRecursion == maxRecursion) {
      return;
    }
    List<String> subReferences = new ArrayList<>();
    references
            .stream()
            .map(id -> repository.getContentById(id))
            .map(ContentDataModel::getReferences)
            .forEach(refList->{
              refList.forEach(refId->{
                subReferences.add(refId.replaceAll(COREMEDIA_CAP_CONTENT,""));
              });
            });
    references.addAll(subReferences);
    handleSubReferences(subReferences, ++currentRecursion, maxRecursion);
  }

  public void addAll(List<String> refs) {
    this.references.addAll(refs);
  }

  public List<String> getReferences() {
    return references;
  }
}
