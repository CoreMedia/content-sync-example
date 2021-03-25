package com.coremedia.blueprint.contentsync.studio.tree {
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncModel;
import com.coremedia.cms.editor.sdk.collectionview.tree.CompoundChildTreeModel;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtil;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.models.LazyLoadingTreeModel;
import com.coremedia.ui.models.NodeChildren;

import mx.resources.ResourceManager;

public class ContentSyncIngestTreeModel implements CompoundChildTreeModel, LazyLoadingTreeModel {

  private static const TREE_ID:String = "contentSyncIngestTreeModelId";
  private var environment:String;
  private var brandFolderId:String;
  private var modelBean:Bean;
  public function ContentSyncIngestTreeModel(env:String, brandFolderId:String, model:Bean) {
    trace("[ContentSyncIngestTreeModel] initialised with environment "+env);
    this.environment = env;
    this.brandFolderId = brandFolderId;
    this.modelBean = model;
  }


  public function setEnabled(enabled:Boolean):void {

  }

  public function isEnabled():Boolean {
    return true;
  }

  public function getTreeId():String {
    return TREE_ID;
  }

  public function getRootId():String {
    return "1";
  }

  public function isRootVisible():Boolean {
    return true;
  }

  public function getText(nodeId:String):String {
    return "";
  }

  public function getIconCls(nodeId:String):String {
    return "";
  }

  public function getTextCls(nodeId:String):String {
    return "";
  }

  public function getChildren(nodeId:String):NodeChildren {
    var model:ContentSyncModel = (getNodeModel(nodeId) as ContentSyncModel);
    if (!model.isLoaded()) {
      model.load();
      return undefined;
    }
    return childrenFor(model.children())
  }

  private function childrenFor(children:Array):NodeChildren {
    var ids:Array = [];
    var names:Object = {};
    var iconCls:Object = {};
    var leafs:Object = {};

    children.forEach(function (it:*):void {
      var typeId:String = it.id.toString();
      var typeName:String = it.name;

      (getNodeModel(typeId) as RemoteBean).load();

      ids.push(typeId);
      names[typeId] = typeName;
      iconCls[typeId] = ResourceManager.getInstance().getString('com.coremedia.cms.editor.Editor', 'CollectionView_folder_icon');
      if (it.type !== 'Folder') {
        leafs[typeId] = true;
        iconCls[typeId] = "content-type-xs " + ContentLocalizationUtil.getIconStyleClassForContentTypeName(it.type);
      }
    });
    return new NodeChildren(ids, names, iconCls, {}, leafs);
  }

  public function getIdPath(nodeId:String):Array {
    return [];
  }

  public function getIdPathFromModel(model:Object):Array {
    return [];
  }

  public function getNodeId(model:Object):String {
    return "";
  }

  public function getNodeModel(nodeId:String):Object {
    return ContentSyncHelper.getContentById(nodeId, environment, modelBean);
  }

  public function isEditable(model:Object):Boolean {
    return false;
  }

  public function rename(model:Object, newName:String, oldName:String, callback:Function):void {
  }

  public function loadNodeModelsById(nodeList:Array):Boolean {
    return false;
  }

  public function setEmptyNodeChildData(childId:String, textsByChildId:Object, iconsByChildId:Object, clsByChildId:Object, leafByChildId:Object, qtipsByChildId:Object):void {
  }
}
}
