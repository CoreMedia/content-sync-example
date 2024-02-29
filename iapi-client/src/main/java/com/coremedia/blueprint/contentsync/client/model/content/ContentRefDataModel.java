package com.coremedia.blueprint.contentsync.client.model.content;

import com.coremedia.blueprint.contentsync.client.IAPIConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentRefDataModel {

  private String id;
  private UUID uuid;
  private String path;
  private String type;

  private String numericId;
  private int numericIdAsInt;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
    numericId = id.substring(IAPIConstants.ID_PREFIX.length());
    numericIdAsInt = Integer.parseInt(numericId);
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @JsonIgnore
  public boolean isFolder() {
    return numericIdAsInt % 2 == 1;
  }

  @JsonIgnore
  public String getNumericId() {
    return numericId;
  }

  @JsonIgnore
  public int getNumericIdAsInt() {
    return numericIdAsInt;
  }
}
