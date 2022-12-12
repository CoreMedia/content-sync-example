package com.coremedia.blueprint.contentsync.client.model.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudToken {
  CloudTokenData data;

  public String getToken() {
    return data.getToken();
  }

  public void setData(CloudTokenData data) {
    this.data = data;
  }

  public static class CloudTokenData {
    String token;
    long expires_at;

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public long getExpires_at() {
      return expires_at;
    }

    public void setExpires_at(long expires_at) {
      this.expires_at = expires_at;
    }
  }
}


