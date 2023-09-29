package com.coremedia.blueprint.contentsync.client.services;

import com.coremedia.blueprint.contentsync.client.IAPIConstants;
import com.coremedia.blueprint.contentsync.client.exception.IAPIInvalidReferenceException;
import com.coremedia.blueprint.contentsync.client.http.IAPIHttpClient;
import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel;

public class IAPIRepositoryImpl implements IAPIRepository {

  private final IAPIHttpClient httpClient;

  public IAPIRepositoryImpl(IAPIHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public ContentDataModel getRoot() {
    return getContentById(IAPIConstants.ROOT_FOLDER_ID);
  }

  @Override
  public ContentDataModel getContentById(String id) {
    return httpClient.executeIdCall(id);
  }

  @Override
  public ContentDataModel getContentByPath(String path) {
    return httpClient.executePathCall(path);
  }


  @Override
  public byte[] getBlob(String property, String contentId) {
    return httpClient.getBlobForUrl(contentId, property);
  }

  @Override
  public ContentDataModel getContentByReference(ContentRefDataModel reference) {
    if (reference.getId().startsWith(IAPIConstants.ID_PREFIX)) {
      return getContentById(reference.getId().substring(IAPIConstants.ID_PREFIX.length()));
    }
    throw new IAPIInvalidReferenceException(reference.getId());
  }

}
