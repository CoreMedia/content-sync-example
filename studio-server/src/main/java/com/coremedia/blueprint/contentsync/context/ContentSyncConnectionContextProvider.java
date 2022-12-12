package com.coremedia.blueprint.contentsync.context;

import com.coremedia.blueprint.contentsync.ContentSyncProperties;
import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import com.coremedia.blueprint.contentsync.client.services.IAPIConnection;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.user.Group;
import com.coremedia.cap.user.User;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ContentSyncConnectionContextProvider {
  private static final Logger LOG = LoggerFactory.getLogger(ContentSyncConnectionContextProvider.class);
  private List<ContentSyncConnectionContext> contextList;
  private ContentSyncConnectionContextProvider() {
    contextList = new ArrayList<>();
  }

  public static ContentSyncConnectionContextProvider init(ContentSyncProperties properties) {
    ContentSyncConnectionContextProvider connectionContextProvider = new ContentSyncConnectionContextProvider();
    try {
      connectionContextProvider.contextList.addAll(properties
              .getHosts()
              .keySet()
              .stream().map(properties::createContextFor)
              .collect(Collectors.toList()));
    } catch (Exception ex) {
      LOG.error("Host/Token/Security Map was not setUp properly ", ex);
    }
    return connectionContextProvider;
  }

  /**
   * Resolve the connection which is applicable for the current user and ident. Ident here
   * is the key which is part of the configuration
   *
   * @param repository The contentRepository to receive the session (user group lookup)
   * @param ident      The key which can be found in the application.properties or / and env
   * @return The IAPIConnection if the user belongs to one of the configured groups
   */
  public IAPIConnection validateAndGet(@NonNull ContentRepository repository, @NonNull String ident) {
    Collection<Group> groups = repository
            .getConnection()
            .getConnectionSession()
            .getUser()
            .getGroups();
    return contextList
            .stream()
            .filter(s -> s.getIdent().equals(ident))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("No valid context found for ident " + ident)).getConnection();
  }
}
