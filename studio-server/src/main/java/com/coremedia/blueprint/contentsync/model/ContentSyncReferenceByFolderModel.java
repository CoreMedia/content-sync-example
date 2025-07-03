package com.coremedia.blueprint.contentsync.model;

import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.services.IAPIRepository;

import java.util.ArrayList;
import java.util.List;

public class ContentSyncReferenceByFolderModel extends ContentSyncReferenceModel {

  public ContentSyncReferenceByFolderModel(IAPIRepository iapiRepository, List<String> initialIdList, int recursion) {
    super(iapiRepository, initialIdList, recursion);
  }

  @Override
  protected void handleSubReferences(List<String> references, final int currentRecursion, final int maxRecursion) {
    List<String> allReferences = new ArrayList<>();
    handleSubReferencesInternal(references, allReferences, currentRecursion, maxRecursion);
    allReferences.stream().filter(id -> Integer.parseInt(id) % 2 == 0).forEach(references::add);
  }

  protected void handleSubReferencesInternal(final List<String> references, List<String> allReferences, final int currentRecursion, final int maxRecursion) {
    List<String> subReferences = new ArrayList<>();
    references
            .stream()
            .map(id -> repository.getContentById(id))
            .map(ContentDataModel::getChildren)
            .forEach(refList->{
              refList.forEach(refId->{
                String contentId = refId.getId().replaceAll(COREMEDIA_CAP_CONTENT, "");
                handleSubReferencesInternal(List.of(contentId), allReferences, currentRecursion, maxRecursion);
                subReferences.add(contentId);
              });
            });
    allReferences.addAll(subReferences);
  }

  public void addAll(List<String> refs) {
    this.references.addAll(refs);
  }

  public List<String> getReferences() {
    return references;
  }
}
