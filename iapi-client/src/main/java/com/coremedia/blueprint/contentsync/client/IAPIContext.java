package com.coremedia.blueprint.contentsync.client;

import com.coremedia.blueprint.contentsync.client.http.IAPIHttpClientImpl;
import com.coremedia.blueprint.contentsync.client.services.IAPIConnection;
import com.coremedia.blueprint.contentsync.client.services.IAPIConnectionImpl;
import com.coremedia.blueprint.contentsync.client.exception.IAPIInitialisedBeforeException;
import com.coremedia.blueprint.contentsync.client.http.IAPIHttpClient;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Main entry point for all applications to get a properly configured IAPIConnection.
 */
public class IAPIContext {

  private final String hostname;
  private final String hostToken;
  private IAPIConnection connection;
  private IAPIHttpClient httpClient;
  private boolean wasInitialisedBefore = false;

  private IAPIContext(String host, String token) {
    hostname = host;
    hostToken = token;
  }

  /**
   * Returns a new IAPIContext for the given parameters
   *
   * @param host,  hostname of the remote ingest-service
   * @param token, provided jwt token for the ingest-service
   * @return The newly created instance of the IAPIContext
   */
  public static synchronized IAPIContext withHostAndToken(@NonNull String host, @NonNull String token) {
    return new IAPIContext(host, token);
  }

  /**
   * If there is any inconvenience with the {@link IAPIConnection}, the developer can change the
   * implementation here.
   *
   * @param conn Custom IAPIConnection if necessary
   * @return this instance.
   */
  public IAPIContext withCustomConnection(@NonNull IAPIConnection conn) {
    this.connection = conn;
    return this;
  }

  /**
   * Used to setp a custom IAPIHttpClient which is used internally to retrieve the json responses from
   * the ingest-service
   *
   * @param client the custom IAPIHttpClient
   * @return the context, this
   */
  public IAPIContext withCustomHttpClient(@NonNull IAPIHttpClient client) {
    this.httpClient = prepareClient(client);
    return this;
  }

  /**
   * Simple init method for the http client performing the requests.
   *
   * @param client a custom implementation of the IAPIClient interface.
   * @return the IAPIHttpClient implementation
   */
  private IAPIHttpClient prepareClient(@NonNull IAPIHttpClient client) {
    client.init(this.hostname, this.hostToken);
    return client;
  }

  /**
   * Setup the IAPIConnection, and make it aware of the configuration.
   *
   * @return the IAPIConnection, can be called several times.
   */
  public IAPIConnection build() {
    if (!wasInitialisedBefore) {
      if (this.httpClient == null) {
        this.httpClient = prepareClient(new IAPIHttpClientImpl());
      }
      if (this.connection == null) {
        this.connection = new IAPIConnectionImpl(this.httpClient);
      }
      wasInitialisedBefore = true;
      return this.connection;
    } else {
      throw new IAPIInitialisedBeforeException();
    }
  }
}
