import ContentSyncModel from "../model/ContentSyncModel";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import FolderTreeNode from "@coremedia/studio-client.ext.ui-components/plugins/FolderTreeNode";
import Deferred from "@jangaroo/ext-ts/Deferred";


abstract class IContentSyncHelper {

  /**
   * Returns the ContentSyncModel for the given id. A ContentSyncModel is basically the representation of
   * a remote Content received via the ingest-service.
   * @param id Id to retrieve. Please note that this id needs to be the numeric representation.
   * @param ident The ident which was configured in the studio webapp.
   * @return The ContentSyncModel for the given id
   */
  abstract getContentById(id:string,ident:string, modelBean:Bean):ContentSyncModel;

  /**
   * Returns the ContentSyncSettings as Array. Please note that a Deferred is returned to provide a bit
   * more of convenience for processing.
   * @return The deferred.
   */
  abstract getContentSyncSettings():Deferred;

  /**
   * Returns all instances of all known sync workflows.
   */
  abstract getRunningInstances():Deferred;

  /**
   * Returns all references and sub references for a given ingest content id
   * @param ident The environment to ask for the content
   * @param id The ingest content id
   * @param recursion Number indicating how many recursions should be performed. Please set to 1
   */
  abstract getReferencesFor(ident:string,id:string,recursion:number, modelBean:Bean):Deferred;

  /**
   * Simple transformer function from a given ContentSyncModel to a FolderTreeNode
   * @param csm The ContentSyncModel to transform
   * @param parent the parent FolderTreeNode.
   */
  abstract contentSyncModel2FolderTreeNode(csm:ContentSyncModel, parent:FolderTreeNode):FolderTreeNode;

  /**
   * Synchronizes the contentList if an add or remove is happening.
   * @param model The bean all information is stored.
   * @param itemList the list of items which needs to be synced.
   */
  abstract synchronizeContentList(model:Bean,  origFnArr:Array<any>):void;

  abstract startWorkflow(modelBean:Bean):void;

  abstract abortWorkflow(id:string):void;
}
export default IContentSyncHelper;
