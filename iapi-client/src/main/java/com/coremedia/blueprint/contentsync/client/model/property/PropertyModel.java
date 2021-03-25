package com.coremedia.blueprint.contentsync.client.model.property;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DatePropertyModel.class, name = DatePropertyModel.TYPE_NAME),
        @JsonSubTypes.Type(value = IntegerPropertyModel.class, name = IntegerPropertyModel.TYPE_NAME),
        @JsonSubTypes.Type(value = LinklistPropertyModel.class, name = LinklistPropertyModel.TYPE_NAME),
        @JsonSubTypes.Type(value = MarkupPropertyModel.class, name = MarkupPropertyModel.TYPE_NAME),
        @JsonSubTypes.Type(value = ServerBlobPropertyModel.class, name = ServerBlobPropertyModel.TYPE_NAME),
        @JsonSubTypes.Type(value = StringPropertyModel.class, name = StringPropertyModel.TYPE_NAME),
        @JsonSubTypes.Type(value = StructPropertyModel.class, name = StructPropertyModel.TYPE_NAME),
        @JsonSubTypes.Type(value = TempBlobPropertyModel.class, name = TempBlobPropertyModel.TYPE_NAME),
        @JsonSubTypes.Type(value = UrlBlobPropertyModel.class, name = UrlBlobPropertyModel.TYPE_NAME)
})
public abstract class PropertyModel {

  private String type;

  public PropertyModel(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
