package com.coremedia.blueprint.contentsync.client.model.property;

import com.coremedia.blueprint.contentsync.client.json.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.ZonedDateTime;

public class DatePropertyModel extends PropertyModel {

  public static final String TYPE_NAME = "Date";


  @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
  private ZonedDateTime value;


  public DatePropertyModel() {
    super(TYPE_NAME);
  }


  public ZonedDateTime getValue() {
    return value;
  }

  public void setValue(ZonedDateTime value) {
    this.value = value;
  }
}
