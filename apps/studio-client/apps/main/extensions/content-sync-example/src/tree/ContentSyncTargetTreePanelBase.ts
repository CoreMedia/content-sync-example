import Bean from "@coremedia/studio-client.client-core/data/Bean";
import PropertyChangeEvent from "@coremedia/studio-client.client-core/data/PropertyChangeEvent";
import FolderTreeNode from "@coremedia/studio-client.ext.ui-components/plugins/FolderTreeNode";
import Ext from "@jangaroo/ext-ts";
import TreeStore from "@jangaroo/ext-ts/data/TreeStore";
import TreePanel from "@jangaroo/ext-ts/tree/Panel";
import { as, asConfig, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import trace from "@jangaroo/runtime/trace";
import ContentSyncPluginResources_properties from "../ContentSyncPluginResources_properties";
import ContentSyncConstants from "../constant/ContentSyncConstants";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import ContentSyncTargetTreePanel from "./ContentSyncTargetTreePanel";

interface ContentSyncTargetTreePanelBaseConfig extends Config<TreePanel>, Partial<Pick<ContentSyncTargetTreePanelBase,
  "modelBean"
>> {
}

class ContentSyncTargetTreePanelBase extends TreePanel {
  declare Config: ContentSyncTargetTreePanelBaseConfig;

  static readonly #ID: string = "id";

  static readonly #CHECK_CHANGE: string = "CheckChange";

  #modelBean: Bean = null;

  get modelBean(): Bean {
    return this.#modelBean;
  }

  set modelBean(value: Bean) {
    this.#modelBean = value;
  }

  #rootObj: Record<string, any> = {
    id: "1",
    text: ContentSyncPluginResources_properties.ContentSync_TargetSyncPanel_Root_Name,
    expanded: true,
  };

  constructor(config: Config<ContentSyncTargetTreePanel> = null) {
    super(config);
    this.modelBean = config.modelBean;
    this.on(ContentSyncTargetTreePanelBase.#CHECK_CHANGE, this.#handleRemoveTargetTree.bind(this));
    this.modelBean.addPropertyChangeListener(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, bind(this, this.#handleContentListChange));
    asConfig((this.getStore() as TreeStore)).root = this.#rootObj;
  }

  /**
   * Handler for the internal CheckChange event. If a uncheck happens, it is necessary to unselect the item
   * in the source sync tree as well. Please note that this "removal" is based on the common mechanism once the contentList is changing (model).
   * @param node The node which was unselected.
   */
  #handleRemoveTargetTree(node: FolderTreeNode): void {
    //handle also all childs which are shown as references
    let nonLeafchildNodes: Array<any> = node.childNodes || [];
    nonLeafchildNodes = nonLeafchildNodes.concat(node);
    ContentSyncHelper.synchronizeContentList(this.modelBean, nonLeafchildNodes);

  }

  /**
   * Event callback to handle any changes on the model Bean which is
   * provided by the ContentSyncEditorBase.
   * @param ev The PropertyChangeEvent which is providing the data old and new value
   */
  #handleContentListChange(ev: PropertyChangeEvent): void {
    const oldPropertyValue: Array<any> = ev.oldValue;
    const newPropertyValue: Array<any> = ev.newValue;

    const contentList: Array<any> = ev.newValue;
    const treeStore = as(this.getStore(), TreeStore);

    if (contentList.length == 0) {
      asConfig((this.getStore() as TreeStore)).root = this.#rootObj;
      return;
    }

    if ((newPropertyValue.length - oldPropertyValue.length) >= 0) {
      const insertContents = contentList.filter((item: any): boolean =>
        (treeStore.findNode(ContentSyncTargetTreePanelBase.#ID, item.data.id) == null),
      );
      insertContents.forEach((current: FolderTreeNode): void =>
        this.#handleAdd(current),
      );
    } else {
      const deleteContents = oldPropertyValue.filter((item: FolderTreeNode): boolean =>
        !ev.newValue.includes(item),
      );
      deleteContents.forEach((item: FolderTreeNode): void =>
        this.#handleRemove(item),
      );
    }
  }

  /**
   * Method executing the removal of a given node.
   * @param item The item to remove.
   */
  #handleRemove(item: FolderTreeNode): void {
    const toBeDeleted = as(this.getStore().getById(item.data.id), FolderTreeNode);
    if (toBeDeleted) {
      toBeDeleted.remove();
    } else {
      trace("[ContentSyncTargetTreePanelBase] could not perform remove");
    }
  }

  /**
   * performs an add operation in case the editor checked a checkbox on the sourceTree
   * @param item The FolderTreeNode to process.
   */
  #handleAdd(item: FolderTreeNode): void {
    const path = ContentSyncTargetTreePanelBase.#getPathToRoot(item);
    let currentChild: any;
    let parent: any = asConfig(this).store.getRoot();
    path.forEach((item: FolderTreeNode): void => {
      currentChild = asConfig(this).store.getById(item.data.id);
      if (!currentChild) {
        currentChild = parent.appendChild(ContentSyncTargetTreePanelBase.#createEntryForId(item));
      }
      parent = currentChild;
    });
  }

  /**
   * This class is not using a TreeModel, so all transformation needs to be done manually.
   * @param item The item which should be created
   */
  static #createEntryForId(item: FolderTreeNode): any {
    const itemId: string = item.data.id;
    const folderNode = new FolderTreeNode({
      id: itemId,
      text: item.data.text,
      expanded: true,
      leaf: item.data.leaf,
      iconCls: item.data.iconCls,
    });
    if (parseInt(itemId) % 2 == 0) {
      Ext.merge(folderNode.data, { checked: true });
    }

    return folderNode;
  }

  /**
   * Method returning the path for a given FolderTreeNode.
   * @param node The FolderTreeNode to calculate the path from
   * @return An array holding all parent folders as FolderTreeNode in reverse order.
   */
  static #getPathToRoot(node: FolderTreeNode): Array<any> {
    const pathList = [];
    let parent = node.parentNode;
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

export default ContentSyncTargetTreePanelBase;
