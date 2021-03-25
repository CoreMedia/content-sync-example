package com.coremedia.blueprint.contentsync.client.model.property;

public class UrlBlobPropertyModel extends PropertyModel {

  public static final String TYPE_NAME = "UrlBlob";


  private boolean isPersistent;
  private String url;
  private String mimeType;
  private int size;


  public UrlBlobPropertyModel() {
    super(TYPE_NAME);
  }


  public boolean isPersistent() {
    return isPersistent;
  }

  public void setPersistent(boolean persistent) {
    isPersistent = persistent;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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
