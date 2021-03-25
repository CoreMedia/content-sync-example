package com.coremedia.blueprint.contentsync.client.model.response;

public class ErrorItem {

  private String errorName;
  private String errorCode;
  private String message;
  private String[] parameters;


  public void setErrorName(String errorName) {
    this.errorName = errorName;
  }

  public String getErrorName() {
    return errorName;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setParameters(String[] parameters) {
    this.parameters = parameters;
  }

  public String[] getParameters() {
    return parameters;
  }
}
