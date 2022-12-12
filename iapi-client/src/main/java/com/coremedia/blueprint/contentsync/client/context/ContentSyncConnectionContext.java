package com.coremedia.blueprint.contentsync.client.context;

import com.coremedia.blueprint.contentsync.client.IAPIContext;
import com.coremedia.blueprint.contentsync.client.services.IAPIConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContentSyncConnectionContext {
  String host;
  String token;
  String cloudHost;
  List<String> groupNames;
  String ident;
  boolean useV2;

  public ContentSyncConnectionContext(String host,
                                      String token,
                                      String cloudHost,
                                      String groupNames,
                                      boolean useV2,
                                      String ident) {
    this.host = host;
    this.token = token;
    this.cloudHost = cloudHost;
    this.ident = ident;
    this.useV2 = useV2;
    this.groupNames = groupNames == null ? Collections.emptyList() : Arrays.asList(groupNames.split(","));
  }

  public String getCloudHost() {
    return cloudHost;
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

  public boolean isUseV2() {
    return useV2;
  }

  public IAPIConnection getConnection() {
    return IAPIContext.withContext(
            this
    ).build();
  }
}
