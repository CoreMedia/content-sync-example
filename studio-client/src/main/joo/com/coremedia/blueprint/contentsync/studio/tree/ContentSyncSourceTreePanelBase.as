package com.coremedia.blueprint.contentsync.studio.tree {
import com.coremedia.blueprint.contentsync.studio.component.ExcludeListRadioGroupBase;
import com.coremedia.blueprint.contentsync.studio.constant.ContentSyncConstants;
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncModel;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncReferenceModel;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncSettings;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.plugins.FolderTreeNode;
import com.coremedia.ui.plugins.FolderTreeStore;

import ext.data.TreeStore;
import ext.tree.TreePanel;
import ext.util.HashMap;

public class ContentSyncSourceTreePanelBase extends TreePanel {

  private static const ITEM_APPEND:String = "ItemAppend";
  private static const CHECK_CHANGE:String = "CheckChange";

  private var treeMap:HashMap = new HashMap();

  [Bindable]
  public var modelBean:Bean;

  public function ContentSyncSourceTreePanelBase(config:ContentSyncSourceTreePanel = null) {
    super(config);
    modelBean = config.modelBean;
    modelBean.addPropertyChangeListener(ContentSyncConstants.SELECTED_ENVIRONMENT, handleModelChange);
    modelBean.addPropertyChangeListener(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, handleContentListChange);
    modelBean.addPropertyChangeListener(ExcludeListRadioGroupBase.CONTENT_TYPE_EXCLUDE, handleExcludes);
    modelBean.addPropertyChangeListener(ExcludeListRadioGroupBase.PROPERTY_EXCLUDE, handleExcludes);

    on(ITEM_APPEND, addCheckBox);
    on(CHECK_CHANGE, onCheckChanged);
  }

  private function handleExcludes(ev:PropertyChangeEvent):void {
    modelBean.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY,[]);
  }

  private function handleContentListChange(ev:PropertyChangeEvent):void {
    var oldValue:* = ev.oldValue;
    var newValue:* = ev.newValue;
    //we are only interested if oldValue - newValue == 1, since the removal is important.
    var itemList:Array = oldValue.filter(function (item:*):Boolean {
      return !newValue.includes(item);
    });
    if (itemList.length > 0) {
      var store:TreeStore = getStore() as TreeStore;
      store.beginUpdate();
      itemList.forEach(function (rem:*):void {
        var node:FolderTreeNode = store.getById(rem.data.id) as FolderTreeNode;
        if (node) {
          node.set("checked", false);
        }
      });
      store.endUpdate();
    }
  }

  private function handleModelChange(changed:PropertyChangeEvent):void {
    var csm:ContentSyncSettings = modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING);
    var selectedValue:String = changed.newValue;
    if (!selectedValue){
      return;
    }
    //if (!treeMap.containsKey(selectedValue)) {
      var tree:TreeStore = new FolderTreeStore(this, new ContentSyncIngestTreeModel(changed.newValue, csm.brandFolderId, modelBean), null, null);
      treeMap.add(selectedValue, tree);
    //}
    modelBean.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, []);
    setStore(treeMap.get(selectedValue) as TreeStore);
  }

  private function onCheckChanged(ev:FolderTreeNode):void {
    var obj:Object = ev.data;
    var isChecked:Boolean = obj.checked;

    var allSelectedContents:Array = modelBean.get(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY);
    if (isChecked) {
      allSelectedContents = [ev].concat(allSelectedContents);
      //calculate the direct references, and the configured recursion depth.
      resolveAndAddReferences(ev);
    } else {
      allSelectedContents = allSelectedContents.filter(function (it:FolderTreeNode):* {
        return it.data.id !== ev.data.id;
      });
    }
    modelBean.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, allSelectedContents);
  }

  private function resolveAndAddReferences(parentFolderTreeNode:FolderTreeNode):void {
    var contentSyncSetting:ContentSyncSettings = modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING);
    ContentSyncHelper.getReferencesFor(contentSyncSetting.identifier,
            parentFolderTreeNode.data.id,
            contentSyncSetting.recursionPartialSync,
            modelBean
    )
            .then(function (item:ContentSyncReferenceModel):void {
              var ref:Array = item.getReferences(contentSyncSetting.identifier, modelBean);
              ref.forEach(function (it:ContentSyncModel):void {
                it.load(function (csm:ContentSyncModel):void {
                  var node:FolderTreeNode = ContentSyncHelper.contentSyncModel2FolderTreeNode(csm, parentFolderTreeNode);
                  var existentContent:Array = modelBean.get(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY);
                  modelBean.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, [node].concat(existentContent));
                });
              });
            });
  }

  private static function addCheckBox(parent:*, node:*):void {
    if (parseInt(node.data.id) % 2 == 0) {
      node.data.checked = false;
    }
  }

  public override function destroy(...params):void {
    super.destroy(params);
    //unregister the append event
    un(ITEM_APPEND, addCheckBox);
    un(CHECK_CHANGE, onCheckChanged);
    modelBean.removePropertyChangeListener(ContentSyncConstants.SELECTED_ENVIRONMENT, handleModelChange);
    modelBean.removePropertyChangeListener(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, handleContentListChange);
    modelBean.removePropertyChangeListener(ExcludeListRadioGroupBase.CONTENT_TYPE_EXCLUDE, handleExcludes);
    modelBean.removePropertyChangeListener(ExcludeListRadioGroupBase.PROPERTY_EXCLUDE, handleExcludes);
  }
}
}
