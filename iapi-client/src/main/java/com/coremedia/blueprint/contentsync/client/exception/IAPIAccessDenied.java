package com.coremedia.blueprint.contentsync.client.exception;

public class IAPIAccessDenied extends Exception{
  public IAPIAccessDenied() {
    super("Cannot query ingest endpoint due to missing priviledges");
  }
}
