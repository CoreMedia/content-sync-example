package com.coremedia.blueprint.contentsync.context;

import com.coremedia.blueprint.contentsync.ContentSyncProperties;
import com.coremedia.blueprint.contentsync.client.services.IAPIConnection;
import com.coremedia.cap.content.ContentRepository;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ContentSyncConnectionContextProvider {
  private static final Logger LOG = LoggerFactory.getLogger(ContentSyncConnectionContextProvider.class);
  private List<ContentSyncConnectionContext> contextList;

  private ContentSyncConnectionContextProvider() {
    contextList = new ArrayList<>();
  }

  public static ContentSyncConnectionContextProvider init(ContentSyncProperties properties) {
    ContentSyncConnectionContextProvider connectionContextProvider = new ContentSyncConnectionContextProvider();
    try {
      properties
              .getHosts()
              .forEach((key, value) -> {
                String host = value;
                String token = properties.getTokens().get(key);
                String groups = properties.getSyncGroups().get(key);
                connectionContextProvider.contextList.add(new ContentSyncConnectionContext(host, token, groups, key));
              });
    } catch (Exception ex) {
      LOG.error("Host/Token/Security Map was not setUp properly ", ex);
    }
    return connectionContextProvider;
  }

  /**
   * TODO: double check the configured users per repository.
   * Resolve the connection which is applicable for the current user and ident. Ident here
   * is the key which is part of the configuration
   *
   * @param repository The contentRepository to receive the session (user group lookup)
   * @param ident      The key which can be found in the application.properties or / and env
   * @return The IAPIConnection if the user belongs to one of the configured groups
   */
  public IAPIConnection validateAndGet(@NonNull ContentRepository repository, @NonNull String ident) {
    return contextList
            .stream()
            .filter(s -> s.getIdent().equals(ident))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("No valid context found for ident " + ident)).getConnection();
  }
}
