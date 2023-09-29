package com.coremedia.blueprint.contentsync.client.exception;

public class IAPIInvalidResponseException extends RuntimeException {

  public IAPIInvalidResponseException() {
    super("Response was invalid");
  }

  public IAPIInvalidResponseException(String details) {
    super("Response was invalid: " + details);
  }

  public IAPIInvalidResponseException(Throwable cause) {
    super(cause);
  }
}
