package com.coremedia.blueprint.contentsync.client.services;

public interface IAPIConnection {

  /**
   * Returns the repository for the actual work.
   */
  IAPIRepository getRepository();
}
