package com.coremedia.blueprint.contentsync.client.http;

import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import com.coremedia.blueprint.contentsync.client.exception.IAPIAccessDenied;
import com.coremedia.blueprint.contentsync.client.exception.IAPIInvalidResponseException;
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

import java.util.function.Function;
import java.util.function.Supplier;


@DefaultAnnotation(NonNull.class)
public class IAPIHttpClientImpl implements IAPIHttpClient {
  private static final Logger LOG = LoggerFactory.getLogger(IAPIHttpClientImpl.class);
  private ConnectionPool connectionPool = new ConnectionPool();
  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";
  private IAPIEndpointHandler handler;
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
    handler = createBaseRequest(context.getHost(),
            context.getToken(),
            IAPIEndpointHandler.class);
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
   * Configure and set up the deserializer.
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
    return executeWithRetry(this::executeCall, () -> handler.getContentByPath(path)).getData();
  }

  public ContentDataModel executeIdCall(String id) {
    return executeWithRetry(this::executeCall, () -> handler.getContentById(id)).getData();
  }

  public byte[] getBlobForUrl(String contentId, String property) {
    return executeWithRetry(this::executeBinaryCall, () -> handler.getBlobUrl(contentId, property));
  }

  private <S,T> T executeWithRetry(Function<S, T> function, Supplier<S> provider) {
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
      LOG.error("cannot process response: " + e.toString());
      throw new IAPIInvalidResponseException(e.toString());
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

}
