package com.coremedia.blueprint.contentsync.client.context;

import com.coremedia.blueprint.contentsync.client.IAPIContext;
import com.coremedia.blueprint.contentsync.client.services.IAPIConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContentSyncConnectionContext {
  String host;
  String token;
  List<String> groupNames;
  String ident;

  public ContentSyncConnectionContext(String host,
                                      String token,
                                      String groupNames,
                                      String ident) {
    this.host = host;
    this.token = token;
    this.ident = ident;
    this.groupNames = groupNames == null ? Collections.emptyList() : Arrays.asList(groupNames.split(","));
  }


  public String getHost() {
    return host;
  }


  public String getToken() {
    return token;
  }


  public List<String> getGroupNames() {
    return groupNames;
  }


  public String getIdent() {
    return ident;
  }

  public IAPIConnection getConnection() {
    return IAPIContext.withContext(this).build();
  }
}
