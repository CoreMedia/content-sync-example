package com.coremedia.blueprint.contentsync.client.services;

public interface IAPIConnection {

  /**
   * Returns the repository for the actual work.
   */
  IAPIRepository getRepository();

  /**
   * Returns the sites service for retrieving all sites. Please note that this sites service is
   * kind of expensive (usage) because of the lack of a search functionality e.g. SOLR, which
   * implies a recursive folder lookup.
   */
  IAPISitesService getSitesService();
}