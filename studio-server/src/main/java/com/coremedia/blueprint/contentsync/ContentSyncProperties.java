package com.coremedia.blueprint.contentsync;

import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * Configuration properties for the content sync. The format is as follows:
 * - ingest.config.hosts[<IDENTIFIER1>] = <HOSTNAME1>
 * - ingest.config.tokens[<IDENTIFIER1>] = <TOKEN_IDENTIFIER1>
 * - ingest.config.syncGroups[<IDENTIFIER1>] = <GROUP_NAMES>
 * - ingest.config.sync2wfs[<SYNC_MODE_NAME>] = <WFS_NAME_TO_EXECUTE>
 */
@ConfigurationProperties(prefix = ContentSyncProperties.INGEST_CONFIG_SECTION)
public class ContentSyncProperties {

  public static final String INGEST_CONFIG_SECTION = "ingest.config";
  Map<String, String> hosts;
  Map<String, String> tokens;
  Map<String, String> cloudHosts;
  Map<String, String> syncGroups;
  Map<String,String>  sync2wfs;
  Map<String,Boolean> useV2;
  public Map<String, String> getSyncGroups() {
    return syncGroups;
  }

  public void setSyncGroups(Map<String, String> syncGroups) {
    this.syncGroups = syncGroups;
  }

  public Map<String, String> getHosts() {
    return hosts;
  }

  public void setHosts(Map<String, String> hosts) {
    this.hosts = hosts;
  }

  public Map<String, String> getTokens() {
    return tokens;
  }

  public void setCloudHosts(Map<String, String> hosts) {
    this.cloudHosts = hosts;
  }

  public Map<String, String> getCloudHosts() {
    return cloudHosts == null ? Collections.emptyMap() : cloudHosts;
  }

  public void setTokens(Map<String, String> tokens) {
    this.tokens = tokens;
  }

  public Map<String, String> getSync2wfs() {
    return sync2wfs;
  }

  public void setSync2wfs(Map<String, String> sync2wfs) {
    this.sync2wfs = sync2wfs;
  }

  public Map<String, Boolean> getUseV2() {
    return useV2;
  }

  public void setUseV2(Map<String, Boolean> useV2) {
    this.useV2 = useV2;
  }

  public ContentSyncConnectionContext createContextFor(@NonNull String ident){
    return new ContentSyncConnectionContext(
            getHosts().getOrDefault(ident,null),
            getTokens().getOrDefault(ident,null),
            getCloudHosts().getOrDefault(ident,"https://api.ps.coremedia.cloud/v1/"),
            getSyncGroups().getOrDefault(ident,"administratoren"),
            getUseV2().getOrDefault(ident,false),
            ident);
  }
}
