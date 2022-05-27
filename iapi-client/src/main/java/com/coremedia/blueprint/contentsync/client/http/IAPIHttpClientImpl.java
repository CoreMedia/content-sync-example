package com.coremedia.blueprint.contentsync.client.http;

import com.coremedia.blueprint.contentsync.client.exception.IAPIInitialisedBeforeException;
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
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

@DefaultAnnotation(NonNull.class)
public class IAPIHttpClientImpl implements IAPIHttpClient {

  private static final Logger LOG = LoggerFactory.getLogger(IAPIHttpClientImpl.class);

  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";
  private IAPIEndpointHandler handler;
  private final ConnectionPool connectionPool = new ConnectionPool();

  @Override
  public void init(String host, String token) {
    if (handler == null) {
      handler = createBaseRequest(host, token);
    } else {
      throw new IAPIInitialisedBeforeException("You are trying to reinitialize the IAPIHttpClient, which is not supported!");
    }
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
  private IAPIEndpointHandler createBaseRequest(String host, String token) {
    JacksonConverterFactory factory = JacksonConverterFactory.create();
    HttpUrl.Builder url = HttpUrl.parse(host).newBuilder();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url.build())
            .client(getClient(token))
            .addConverterFactory(factory)
            .build();

    return retrofit.create(IAPIEndpointHandler.class);
  }

  public ContentDataModel executePathCall(String path) {
    return executeCall(handler.getContentByPath(path)).getData();
  }

  public ContentDataModel executeIdCall(String id) {
    return executeCall(handler.getContentById(id)).getData();
  }

  @Override
  public byte[] getBlobForUrl(String contentId, String property) {
    return executeBinaryCall(handler.getBlobUrl(contentId, property));
  }

  private byte[] executeBinaryCall(Call<ResponseBody> call) {
    try {
      Response<ResponseBody> response = call.execute();
      if (response.isSuccessful() && response.body()!=null) {
        return response.body().bytes();
      } else if (response.code() == 400) {
        return new byte[0];
      }
    } catch (Exception ex) {
      LOG.error("cannot process request/response for binary data");
    }
    throw new IAPIInvalidResponseException();
  }

  private ResponseDataModel<ContentDataModel> executeCall(Call<ResponseDataModel<ContentDataModel>> call) {
    try {
      Response<ResponseDataModel<ContentDataModel>> response = call.execute();
      if (response.isSuccessful()) {
        return response.body();
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
}
