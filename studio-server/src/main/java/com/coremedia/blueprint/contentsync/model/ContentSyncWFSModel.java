package com.coremedia.blueprint.contentsync.model;

import com.coremedia.cap.workflow.Process;

import java.util.TimeZone;

public class ContentSyncWFSModel {

  String name;
  TimeZone creationDate;
  String id;

  public ContentSyncWFSModel(){

  }

  public ContentSyncWFSModel(Process process){
    name = process.getType().getName();
    creationDate = process.getCreationDate().getTimeZone();
    id = process.getId().replaceAll("coremedia:///cap/process/","");;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setCreationDate(TimeZone creationDate) {
    this.creationDate = creationDate;
  }

  public String getName() {
    return name;
  }

  public TimeZone getCreationDate() {
    return creationDate;
  }
}
