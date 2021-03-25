package com.coremedia.blueprint.contentsync.client.model.property;

public class TempBlobPropertyModel extends PropertyModel {

  public static final String TYPE_NAME = "TempBlob";


  private String id;
  private String mimeType;
  private int size;


  public TempBlobPropertyModel() {
    super(TYPE_NAME);
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
