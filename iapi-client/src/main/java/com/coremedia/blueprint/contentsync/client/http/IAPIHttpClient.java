package com.coremedia.blueprint.contentsync.client.http;

import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public interface IAPIHttpClient {
  /**
   * Used by the framework to initialize the implementation with the necessary information.
   *
   * @param host  the hostname
   * @param token the JWT used to query the ingest-service found under the given host above.
   */
  void init(String host, String token);

  ContentDataModel executePathCall(String path);

  ContentDataModel executeIdCall(String id);

  byte[] getBlobForUrl(String contentId,String property);
}
