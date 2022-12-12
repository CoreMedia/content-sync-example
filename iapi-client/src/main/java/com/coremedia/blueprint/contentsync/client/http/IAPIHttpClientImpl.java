package com.coremedia.blueprint.contentsync.client.http;

import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import com.coremedia.blueprint.contentsync.client.exception.IAPIAccessDenied;
import com.coremedia.blueprint.contentsync.client.exception.IAPIInitialisedBeforeException;
import com.coremedia.blueprint.contentsync.client.exception.IAPIInvalidResponseException;
import com.coremedia.blueprint.contentsync.client.model.auth.CloudToken;
import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.response.ResponseDataModel;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.*;

import java.util.concurrent.*;

@DefaultAnnotation(NonNull.class)
public class IAPIHttpClientImpl implements IAPIHttpClient {
  private static final Logger LOG = LoggerFactory.getLogger(IAPIHttpClientImpl.class);
  private ConnectionPool connectionPool = new ConnectionPool();
  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";
  private IAPIEndpointHandler handler;
  private ICloudTokenHandler cloudTokenHandler;
  private ContentSyncConnectionContext context;

  @Override
  public void init(ContentSyncConnectionContext context) {
    this.context = context;
    if (handler == null && cloudTokenHandler == null) {
      if (context.isUseV2()) {
        cloudTokenHandler = createBaseRequest(context.getCloudHost(), context.getToken(), ICloudTokenHandler.class);
      }
    } else {
      throw new IAPIInitialisedBeforeException("You are trying to reinitialize the IAPIHttpClient, which is not supported!");
    }
  }

  private void initHandler() throws IAPIAccessDenied {
    handler = createBaseRequest(context.getHost(),
            context.isUseV2() ? executeAuthCall(120).getToken() : context.getToken(),
            IAPIEndpointHandler.class);
  }

  @Override
  public CloudToken executeAuthCall(long ttl) throws IAPIAccessDenied {
    return executeCall(cloudTokenHandler.getCloudAuthToken(ttl));
  }
//------------------------ Boilerplate stuff necessary to perform the requests defined in the interface

  /**
   * Configuration of the client. Mainly used to add the bearer token.
   *
   * @param token the bearer/JWT token handed
   * @return Instance of the OkHttpClient used to perform the actual request
   */
  private OkHttpClient getClient(String token) {
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    httpClient.addInterceptor((chain) -> {
      Request original = chain.request();
      Request request = original.newBuilder()
              .header(AUTHORIZATION, BEARER + token)
              .method(original.method(), original.body())
              .build();

      return chain.proceed(request);
    });
    return httpClient.connectionPool(connectionPool).build();
  }

  /**
   * Configure and setup the deserializer.
   */
  private <T> T createBaseRequest(String host, String token, Class<T> clazz) {
    JacksonConverterFactory factory = JacksonConverterFactory.create();
    HttpUrl.Builder url = HttpUrl.parse(host).newBuilder();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url.build())
            .client(getClient(token))
            .addConverterFactory(factory)
            .build();

    return retrofit.create(clazz);
  }

  public ContentDataModel executePathCall(String path) {
    try {
      return executeCall(handler.getContentByPath(path)).getData();
    } catch (IAPIAccessDenied ex) {
      try {
        initHandler();
        return executeCall(handler.getContentByPath(path)).getData();
      } catch (IAPIAccessDenied iaex) {
        LOG.error("could not update token.");
      }
    }
    return null;
  }

  public ContentDataModel executeIdCall(String id) {
    try {
      return executeCall(handler.getContentById(id)).getData();
    } catch (IAPIAccessDenied iapex) {
      try {
        initHandler();
        return executeCall(handler.getContentById(id)).getData();
      } catch (IAPIAccessDenied iaex) {
        LOG.error("could not update token.");
      }
    }
    return null;
  }

  @Override
  public byte[] getBlobForUrl(String contentId, String property) {
    try {
      return executeBinaryCall(handler.getBlobUrl(contentId, property));
    } catch (IAPIAccessDenied iaex) {
      try {
        initHandler();
        return executeBinaryCall(handler.getBlobUrl(contentId, property));
      } catch (IAPIAccessDenied iex) {
        LOG.error("could not update token.");
      }
    }
    return null;
  }

  private byte[] executeBinaryCall(Call<ResponseBody> call) throws IAPIAccessDenied {
    try {
      Response<ResponseBody> response = call.execute();
      if (response.isSuccessful()) {
        assert response.body() != null;
        return response.body().bytes();
      } else if (response.code() == 400) {
        return new byte[0];
      } else if (response.code() == 403) {
        throw new IAPIAccessDenied();
      }
    } catch (Exception ex) {
      LOG.error("cannot process request/response for binary data");
    }
    throw new IAPIInvalidResponseException();
  }

  private <T> T executeCall(Call<T> call) throws IAPIAccessDenied {
    try {
      Response<T> response = (Response<T>) call.execute();
      if (response.isSuccessful()) {
        return response.body();
      } else if (response.code() == 403) {
        throw new IAPIAccessDenied();
      }
    } catch (Exception e) {
      LOG.error("cannot process response");
    }
    throw new IAPIInvalidResponseException();
  }

  public interface IAPIEndpointHandler {

    @GET("content/{id}")
    Call<ResponseDataModel<ContentDataModel>> getContentById(@Path("id") String id);

    @GET("content")
    Call<ResponseDataModel<ContentDataModel>> getContentByPath(@Query("path") String path);

    @GET("content/{id}/properties/{propertyName}/data")
    Call<ResponseBody> getBlobUrl(@Path("id") String id, @Path("propertyName") String propertyName);
  }

  public interface ICloudTokenHandler {
    @POST("token")
    Call<CloudToken> getCloudAuthToken(@Body long ttl);
  }

}
