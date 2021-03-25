package com.coremedia.blueprint.contentsync;

import com.coremedia.blueprint.contentsync.resources.ContentSyncResource;
import com.coremedia.cap.content.ContentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentSyncConfiguration {

  @Bean
  @SuppressWarnings("all")
  public ContentSyncResource contentSyncResource(ContentRepository repository, ContentSyncProperties properties) {
    return new ContentSyncResource(repository, properties);
  }

  @Bean
  public ContentSyncProperties contentSyncConfigProperties() {
    return new ContentSyncProperties();
  }
}
