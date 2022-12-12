import {as, mixin} from "@jangaroo/runtime";
import {AnyFunction} from "@jangaroo/runtime/types";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import ContentSyncModel from "../model/ContentSyncModel";
import ContentLocalizationUtil from "@coremedia/studio-client.cap-base-models/content/ContentLocalizationUtil";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import LazyLoadingTreeModel from "@coremedia/studio-client.ext.ui-components/models/LazyLoadingTreeModel";
import NodeChildren from "@coremedia/studio-client.ext.ui-components/models/NodeChildren";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import CompoundChildTreeModel
  from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/CompoundChildTreeModel";
import trace from "@jangaroo/runtime/trace";


class ContentSyncIngestTreeModel implements CompoundChildTreeModel, LazyLoadingTreeModel {

  static readonly #TREE_ID: string = "contentSyncIngestTreeModelId";
  #environment: string = null;
  #brandFolderId: string = null;
  #modelBean: Bean = null;

  constructor(env: string, brandFolderId: string, model: Bean) {
    trace("[ContentSyncIngestTreeModel] initialised with environment " + env);
    this.#environment = env;
    this.#brandFolderId = brandFolderId;
    this.#modelBean = model;
  }


  setEnabled(enabled: boolean): void {

  }

  isEnabled(): boolean {
    return true;
  }

  getTreeId(): string {
    return ContentSyncIngestTreeModel.#TREE_ID;
  }

  getRootId(): string {
    return "1";
  }

  isRootVisible(): boolean {
    return true;
  }

  getText(nodeId: string): string {
    return "";
  }

  getIconCls(nodeId: string): string {
    return "";
  }

  getTextCls(nodeId: string): string {
    return "";
  }

  getChildren(nodeId: string): NodeChildren {
    var model = (as(this.getNodeModel(nodeId), ContentSyncModel));
    if (!model.isLoaded()) {
      model.load();
      return undefined;
    }
    return this.#childrenFor(model.children());
  }

  #childrenFor(children: Array<any>): NodeChildren {
    var ids = [];
    var names: Record<string, any> = {};
    var iconCls: Record<string, any> = {};
    var leafs: Record<string, any> = {};

    children.forEach((it: any): void => {
      var typeId: string = it.id.toString();
      var typeName: string = it.name;

      as(this.getNodeModel(typeId), RemoteBean).load();

      ids.push(typeId);
      names[typeId] = typeName;
      iconCls[typeId] = Editor_properties.CollectionView_folder_icon;
      if (it.type !== "Folder") {
        leafs[typeId] = true;
        iconCls[typeId] = "content-type-xs " + ContentLocalizationUtil.getIconStyleClassForContentTypeName(it.type);
      }
    });
    return new NodeChildren(ids, names, iconCls, {}, leafs);
  }

  getIdPath(nodeId: string): Array<any> {
    return [];
  }

  getIdPathFromModel(model: any): Array<any> {
    return [];
  }

  getNodeId(model: any): string {
    return "";
  }

  getNodeModel(nodeId: string): any {
    return ContentSyncHelper.getContentById(nodeId, this.#environment, this.#modelBean);
  }

  isEditable(model: any): boolean {
    return false;
  }

  rename(model: any, newName: string, oldName: string, callback: AnyFunction): void {
  }

  loadNodeModelsById(nodeList: Array<any>): boolean {
    return false;
  }

  setEmptyNodeChildData(childId: string, textsByChildId: any, iconsByChildId: any, clsByChildId: any, leafByChildId: any, qtipsByChildId: any): void {
  }
}

mixin(ContentSyncIngestTreeModel, CompoundChildTreeModel, LazyLoadingTreeModel);

export default ContentSyncIngestTreeModel;
