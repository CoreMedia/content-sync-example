package com.coremedia.blueprint.contentsync.studio.model {
import com.coremedia.ui.data.RemoteBean;

public interface IContentSyncModel extends RemoteBean{

  /**
   * Id of the given remote content delivered by the ingest-service
   * @return the numeric id
   */
  function getId():String;

  /**
   * Name of the remote content delivered by the ingest-service
   * @return the name extracted from the path
   */
  function getName():String;

  /**
   * Information if the given remote content is a folder, used within a tree.
   * @return true in case the remote content is a folder, otherwise false.
   */
  function isLeaf():Boolean;

  /**
   * Children information if the remote content is a folder. Please note that
   * children is never null.
   */
  function children():Array;

  /**
   * Type of the current content. Usefull for the calculation of the icon.
   * @return Type of the remote content delivered by the ingest-server
   */
  function getType():String;

  function getContentId():String;
}
}
