package com.coremedia.blueprint.contentsync.studio.tree {
import com.coremedia.blueprint.contentsync.studio.constant.ContentSyncConstants;
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.plugins.FolderTreeNode;

import ext.Ext;
import ext.data.NodeInterface;
import ext.data.TreeStore;
import ext.tree.TreePanel;

public class ContentSyncTargetTreePanelBase extends TreePanel {

  private static const ID:String = "id";
  private static const CHECK_CHANGE:String = "CheckChange";

  [Bindable]
  public var modelBean:Bean;

  private var rootObj:Object = {
    id: "1",
    text: resourceManager.getString('com.coremedia.blueprint.contentsync.studio.ContentSyncPluginResources', 'ContentSync_TargetSyncPanel_Root_Name'),
    expanded: true
  };

  public function ContentSyncTargetTreePanelBase(config:ContentSyncTargetTreePanel = null) {
    super(config);
    this.modelBean = config.modelBean;
    on(CHECK_CHANGE, handleRemoveTargetTree);
    this.modelBean.addPropertyChangeListener(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, handleContentListChange);
    (getStore() as TreeStore).root = rootObj;
  }

  /**
   * Handler for the internal CheckChange event. If a uncheck happens, it is necessary to unselect the item
   * in the source sync tree as well. Please note that this "removal" is based on the common mechanism once the contentList is changing (model).
   * @param node The node which was unselected.
   */
  private function handleRemoveTargetTree(node:FolderTreeNode):void {
    //handle also all childs which are shown as references
    var nonLeafchildNodes:Array = node.childNodes || [];
    nonLeafchildNodes = nonLeafchildNodes.concat(node);
    ContentSyncHelper.synchronizeContentList(modelBean, nonLeafchildNodes);

  }

  /**
   * Event callback to handle any changes on the model Bean which is
   * provided by the ContentSyncEditorBase.
   * @param ev The PropertyChangeEvent which is providing the data old and new value
   */
  private function handleContentListChange(ev:PropertyChangeEvent):void {
    var oldPropertyValue:Array = ev.oldValue;
    var newPropertyValue:Array = ev.newValue;

    var contentList:Array = ev.newValue;
    var treeStore:TreeStore = getStore() as TreeStore;

    if (contentList.length == 0) {
      (getStore() as TreeStore).root = rootObj;
      return;
    }

    if ((newPropertyValue.length - oldPropertyValue.length) >= 0) {
      var insertContents:Array = contentList.filter(function (item:*):Boolean {
        return (treeStore.findNode(ID, item.data.id) == null);
      });
      insertContents.forEach(function (current:FolderTreeNode):void {
        handleAdd(current);
      });
    } else {
      var deleteContents:Array = oldPropertyValue.filter(function (item:FolderTreeNode):Boolean {
        return !ev.newValue.includes(item);
      });
      deleteContents.forEach(function (item:FolderTreeNode):void {
        handleRemove(item);
      })
    }
  }

  /**
   * Method executing the removal of a given node.
   * @param item The item to remove.
   */
  private function handleRemove(item:FolderTreeNode):void {
    var toBeDeleted:FolderTreeNode = getStore().getById(item.data.id) as FolderTreeNode;
    if (toBeDeleted) {
      toBeDeleted.remove();
    } else {
      trace("[ContentSyncTargetTreePanelBase] could not perform remove")
    }
  }

  /**
   * performs an add operation in case the editor checked a checkbox on the sourceTree
   * @param item The FolderTreeNode to process.
   */
  private function handleAdd(item:FolderTreeNode):void {
    var path:Array = getPathToRoot(item);
    var currentChild:*;
    var parent:* = store.getRoot();
    path.forEach(function (item:FolderTreeNode):void {
      currentChild = store.getById(item.data.id);
      if (!currentChild) {
        currentChild = parent.appendChild(createEntryForId(item));
      }
      parent = currentChild;
    });
  }

  /**
   * This class is not using a TreeModel, so all transformation needs to be done manually.
   * @param item The item which should be created
   */
  private static function createEntryForId(item:FolderTreeNode):Object {
    var itemId:String = item.data.id;
    var folderNode:FolderTreeNode = new FolderTreeNode({
      id: itemId,
      text: item.data.text,
      expanded: true,
      leaf: item.data.leaf,
      iconCls: item.data.iconCls
    });
    if (parseInt(itemId) % 2 == 0) {
      Ext.merge(folderNode.data, {
        checked: true
      });
    }

    return folderNode;
  }

  /**
   * Method returning the path for a given FolderTreeNode.
   * @param node The FolderTreeNode to calculate the path from
   * @return An array holding all parent folders as FolderTreeNode in reverse order.
   */
  private static function getPathToRoot(node:FolderTreeNode):Array {
    var pathList:Array = [];
    var parent:NodeInterface = node.parentNode;
    if (!parent) {
      return pathList;
    }
    pathList.push(node);
    while (!parent.isRoot()) {
      pathList.push(parent);
      parent = parent.parentNode;
    }
    return pathList.reverse();
  }
}
}
