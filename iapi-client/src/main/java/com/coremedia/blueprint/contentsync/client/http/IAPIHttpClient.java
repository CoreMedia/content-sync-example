package com.coremedia.blueprint.contentsync.client.http;

import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import com.coremedia.blueprint.contentsync.client.model.auth.CloudToken;
import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public interface IAPIHttpClient {
  /**
   * Used by the framework to initialize the implementation with the necessary information.
   *
   * @param context the configuration object
   */
  void init(ContentSyncConnectionContext context);


  ContentDataModel executePathCall(String path);

  ContentDataModel executeIdCall(String id);

  CloudToken executeAuthCall(long ttl);

  byte[] getBlobForUrl(String contentId, String property);
}
