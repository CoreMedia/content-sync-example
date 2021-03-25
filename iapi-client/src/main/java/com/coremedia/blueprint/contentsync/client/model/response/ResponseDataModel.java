package com.coremedia.blueprint.contentsync.client.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ResponseDataModel<E> {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private E data;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<ErrorItem> errors;


  public E getData() {
    return data;
  }

  public void setData(E data) {
    this.data = data;
  }

  public List<ErrorItem> getErrors() {
    return errors;
  }

  public void setErrors(List<ErrorItem> errors) {
    this.errors = errors;
  }
}
