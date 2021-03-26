package com.coremedia.blueprint.contentsync.client.exception;

public class IAPIInvalidResponseException extends RuntimeException {

  public IAPIInvalidResponseException() {
    super("Response was invalid");
  }

  public IAPIInvalidResponseException(Throwable cause) {
    super(cause);
  }
}
