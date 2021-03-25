package com.coremedia.blueprint.contentsync.client.model.property;

public class StringPropertyModel extends PropertyModel {

  public static final String TYPE_NAME = "String";


  private String value;


  public StringPropertyModel() {
    super(TYPE_NAME);
  }


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
