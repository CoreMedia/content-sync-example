package com.coremedia.blueprint.contentsync.client.services;

import com.coremedia.blueprint.contentsync.client.http.IAPIHttpClient;

public class IAPIConnectionImpl implements IAPIConnection {

  private final IAPIHttpClient httpClient;

  public IAPIConnectionImpl(IAPIHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public IAPIRepository getRepository() {
    return new IAPIRepositoryImpl(httpClient);
  }

  @Override
  public IAPISitesService getSitesService() {
    return null;
  }


}
