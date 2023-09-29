package com.coremedia.blueprint.contentsync.client.exception;

public class IAPIAccessDenied extends RuntimeException{
  public IAPIAccessDenied() {
    super("Cannot query ingest endpoint due to missing privileges");
  }
}
