package com.coremedia.blueprint.contentsync.client.services;

import com.coremedia.blueprint.contentsync.client.IAPIConstants;
import com.coremedia.blueprint.contentsync.client.model.auth.CloudToken;
import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel;

import java.util.List;
import java.util.stream.Collectors;

public interface IAPIRepository {

  /**
   * Returns the root folder of the repository.
   */
  ContentDataModel getRoot();

  /**
   * Returns a {@link ContentDataModel} for the given id.
   */
  ContentDataModel getContentById(String id);

  /**
   * Returns a {@link ContentDataModel} for the given path.
   */
  ContentDataModel getContentByPath(String path);

  byte[] getBlob(String property, String contentId);

  /**
   * Returns a {@link ContentDataModel} for the given reference.
   */
  ContentDataModel getContentByReference(ContentRefDataModel reference);

  /**
   * Returns a list of {@link ContentDataModel}s for the given ids.
   */
  default List<ContentDataModel> getContentsByIds(List<String> ids) {
    return ids.stream().map(this::getContentById).collect(Collectors.toList());
  }

  /**
   * Returns a list of {@link ContentDataModel}s for the given paths.
   */
  default List<ContentDataModel> getContentsByPaths(List<String> paths) {
    return paths.stream().map(this::getContentByPath).collect(Collectors.toList());
  }

  /**
   * Returns a list of {@link ContentDataModel}s for the given references.
   */
  default List<ContentDataModel> getContentsByReferences(List<ContentRefDataModel> references) {
    return references.stream().map(this::getContentByReference).collect(Collectors.toList());
  }

  CloudToken getCloudAccessToken(long ttl);
}
