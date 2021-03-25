package com.coremedia.blueprint.contentsync.studio.helper {
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncModel;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.plugins.FolderTreeNode;

import ext.Deferred;

public interface IContentSyncHelper {

  /**
   * Returns the ContentSyncModel for the given id. A ContentSyncModel is basically the representation of
   * a remote Content received via the ingest-service.
   * @param id Id to retrieve. Please note that this id needs to be the numeric representation.
   * @param ident The ident which was configured in the studio webapp.
   * @return The ContentSyncModel for the given id
   */
  function getContentById(id:String,ident:String, modelBean:Bean):ContentSyncModel;

  /**
   * Returns the ContentSyncSettings as Array. Please note that a Deferred is returned to provide a bit
   * more of convenience for processing.
   * @return The deferred.
   */
  function getContentSyncSettings():Deferred;

  /**
   * Returns all instances of all known sync workflows.
   */
  function getRunningInstances():Deferred;

  /**
   * Returns all references and sub references for a given ingest content id
   * @param ident The environment to ask for the content
   * @param id The ingest content id
   * @param recursion Number indicating how many recursions should be performed. Please set to 1
   */
  function getReferencesFor(ident:String,id:String,recursion:Number, modelBean:Bean):Deferred;

  /**
   * Simple transformer function from a given ContentSyncModel to a FolderTreeNode
   * @param csm The ContentSyncModel to transform
   * @param parent the parent FolderTreeNode.
   */
  function contentSyncModel2FolderTreeNode(csm:ContentSyncModel, parent:FolderTreeNode):FolderTreeNode;

  /**
   * Synchronizes the contentList if an add or remove is happening.
   * @param model The bean all information is stored.
   * @param itemList the list of items which needs to be synced.
   */
  function synchronizeContentList(model:Bean, item:FolderTreeNode):void;

  function startWorkflow(modelBean:Bean):void;

  function abortWorkflow(id:String):void
}
}
