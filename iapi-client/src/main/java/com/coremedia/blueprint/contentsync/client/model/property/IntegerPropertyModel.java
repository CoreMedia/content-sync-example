package com.coremedia.blueprint.contentsync.client.model.property;

public class IntegerPropertyModel extends PropertyModel {

  public static final String TYPE_NAME = "Integer";


  private Integer value;


  public IntegerPropertyModel() {
    super(TYPE_NAME);
  }


  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }
}
