package com.coremedia.blueprint.contentsync.client.exception;

public class IAPIInitialisedBeforeException extends RuntimeException {

  public IAPIInitialisedBeforeException() {
    super("IAPI was initialised before, consider to use only the IAPIConnection");
  }

  public IAPIInitialisedBeforeException(String message) {
    super(message);
  }
}
