package com.coremedia.blueprint.contentsync.client.model.property;

public class ServerBlobPropertyModel extends PropertyModel {

  public static final String TYPE_NAME = "ServerBlob";


  private String url;
  private String mimeType;
  private int size;
  private String eTag;


  public ServerBlobPropertyModel() {
    super(TYPE_NAME);
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

  public String getETag() {
    return eTag;
  }

  public void setETag(String md5) {
    this.eTag = eTag;
  }
}
