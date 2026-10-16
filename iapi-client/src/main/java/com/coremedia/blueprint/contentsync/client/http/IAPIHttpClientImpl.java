package com.coremedia.blueprint.contentsync.client.http;

import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import com.coremedia.blueprint.contentsync.client.exception.IAPIAccessDenied;
import com.coremedia.blueprint.contentsync.client.exception.IAPIInvalidResponseException;
import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.response.ResponseDataModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;


@DefaultAnnotation(NonNull.class)
public class IAPIHttpClientImpl implements IAPIHttpClient {
  private static final Logger LOG = LoggerFactory.getLogger(IAPIHttpClientImpl.class);
  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";
  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);
  private static final TypeReference<ResponseDataModel<ContentDataModel>> CONTENT_RESPONSE_TYPE = new TypeReference<>() {
  };

  private final ObjectMapper objectMapper = new ObjectMapper();
  private HttpClient httpClient;
  private URI baseUri;
  private String token;
  private ContentSyncConnectionContext context;

  @Override
  public void init(ContentSyncConnectionContext context) {
    this.context = context;

    try {
      initHandler();
    } catch (IAPIAccessDenied ex) {
      throw new RuntimeException("init failed");
    }
  }

  private void initHandler() throws IAPIAccessDenied {
    String host = context.getHost();
    baseUri = URI.create(host.endsWith("/") ? host : host + "/");
    token = context.getToken();
    if (httpClient == null) {
      // a single client instance is reused for all requests, so connections are pooled
      httpClient = HttpClient.newBuilder()
              .connectTimeout(CONNECT_TIMEOUT)
              .followRedirects(HttpClient.Redirect.NORMAL)
              .build();
    }
  }

  public ContentDataModel executePathCall(String path) {
    ResponseDataModel<ContentDataModel> response = executeWithRetry(this::executeCall, () -> request("content?path=" + encode(path)));
    return response == null ? null : response.getData();
  }

  public ContentDataModel executeIdCall(String id) {
    ResponseDataModel<ContentDataModel> response = executeWithRetry(this::executeCall, () -> request("content/" + encode(id)));
    return response == null ? null : response.getData();
  }

  public byte[] getBlobForUrl(String contentId, String property) {
    return executeWithRetry(this::executeBinaryCall,
            () -> request("content/" + encode(contentId) + "/properties/" + encode(property) + "/data"));
  }

  private <S, T> T executeWithRetry(Function<S, T> function, Supplier<S> provider) {
    try {
      return function.apply(provider.get());
    } catch (IAPIAccessDenied iaex) {
      try {
        initHandler();
        return function.apply(provider.get());
      } catch (IAPIAccessDenied iex) {
        LOG.warn("could not update token.");
        LOG.debug("could not update token.", iex);
      }
    }
    return null;
  }

  /**
   * Builds a GET request relative to the configured host and adds the bearer token.
   */
  private HttpRequest request(String relativePath) {
    return HttpRequest.newBuilder(baseUri.resolve(relativePath))
            .timeout(REQUEST_TIMEOUT)
            .header(AUTHORIZATION, BEARER + token)
            .GET()
            .build();
  }

  private byte[] executeBinaryCall(HttpRequest request) throws IAPIAccessDenied {
    HttpResponse<byte[]> response = send(request);
    if (isSuccessful(response)) {
      return response.body();
    } else if (response.statusCode() == 400) {
      return new byte[0];
    } else if (response.statusCode() == 403) {
      throw new IAPIAccessDenied();
    }
    LOG.error("cannot process request/response for binary data, status {}", response.statusCode());
    throw new IAPIInvalidResponseException();
  }

  private ResponseDataModel<ContentDataModel> executeCall(HttpRequest request) throws IAPIAccessDenied {
    HttpResponse<byte[]> response = send(request);
    if (isSuccessful(response)) {
      try {
        return objectMapper.readValue(response.body(), CONTENT_RESPONSE_TYPE);
      } catch (IOException e) {
        LOG.error("cannot process response: " + e);
        throw new IAPIInvalidResponseException(e.toString());
      }
    } else if (response.statusCode() == 403) {
      throw new IAPIAccessDenied();
    }
    throw new IAPIInvalidResponseException("status " + response.statusCode());
  }

  private HttpResponse<byte[]> send(HttpRequest request) {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    } catch (IOException e) {
      LOG.error("cannot perform request " + request.uri() + ": " + e);
      throw new IAPIInvalidResponseException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IAPIInvalidResponseException(e);
    }
  }

  private static boolean isSuccessful(HttpResponse<?> response) {
    return response.statusCode() >= 200 && response.statusCode() < 300;
  }

  /**
   * Encodes a single path segment or query value (spaces as %20, like the former Retrofit client).
   */
  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
  }

}
