import Bean from "@coremedia/studio-client.client-core/data/Bean";
import PropertyChangeEvent from "@coremedia/studio-client.client-core/data/PropertyChangeEvent";
import FolderTreeNode from "@coremedia/studio-client.ext.ui-components/plugins/FolderTreeNode";
import FolderTreeStore from "@coremedia/studio-client.ext.ui-components/plugins/FolderTreeStore";
import TreeStore from "@jangaroo/ext-ts/data/TreeStore";
import TreePanel from "@jangaroo/ext-ts/tree/Panel";
import HashMap from "@jangaroo/ext-ts/util/HashMap";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ExcludeListRadioGroupBase from "../component/ExcludeListRadioGroupBase";
import ContentSyncConstants from "../constant/ContentSyncConstants";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import ContentSyncModel from "../model/ContentSyncModel";
import ContentSyncReferenceModel from "../model/ContentSyncReferenceModel";
import ContentSyncSettings from "../model/ContentSyncSettings";
import ContentSyncIngestTreeModel from "./ContentSyncIngestTreeModel";
import ContentSyncSourceTreePanel from "./ContentSyncSourceTreePanel";

interface ContentSyncSourceTreePanelBaseConfig extends Config<TreePanel>, Partial<Pick<ContentSyncSourceTreePanelBase,
  "modelBean"
>> {
}

class ContentSyncSourceTreePanelBase extends TreePanel {
  declare Config: ContentSyncSourceTreePanelBaseConfig;

  static readonly #ITEM_APPEND: string = "ItemAppend";

  static readonly #CHECK_CHANGE: string = "CheckChange";

  #treeMap: HashMap = new HashMap();

  #modelBean: Bean = null;

  get modelBean(): Bean {
    return this.#modelBean;
  }

  set modelBean(value: Bean) {
    this.#modelBean = value;
  }

  constructor(config: Config<ContentSyncSourceTreePanel> = null) {
    super(config);
    this.modelBean = config.modelBean;
    this.modelBean.addPropertyChangeListener(ContentSyncConstants.SELECTED_ENVIRONMENT, bind(this, this.#handleModelChange));
    this.modelBean.addPropertyChangeListener(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, bind(this, this.#handleContentListChange));
    this.modelBean.addPropertyChangeListener(ExcludeListRadioGroupBase.CONTENT_TYPE_EXCLUDE, bind(this, this.#handleExcludes));
    this.modelBean.addPropertyChangeListener(ExcludeListRadioGroupBase.PROPERTY_EXCLUDE, bind(this, this.#handleExcludes));

    this.on(ContentSyncSourceTreePanelBase.#ITEM_APPEND, ContentSyncSourceTreePanelBase.#addCheckBox);
    this.on(ContentSyncSourceTreePanelBase.#CHECK_CHANGE, bind(this, this.#onCheckChanged));
  }

  #handleExcludes(ev: PropertyChangeEvent): void {
    this.modelBean.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, []);
  }

  #handleContentListChange(ev: PropertyChangeEvent): void {
    const oldValue = ev.oldValue;
    const newValue = ev.newValue;
    //we are only interested if oldValue - newValue == 1, since the removal is important.
    const itemList: Array<any> = oldValue.filter((item: any): boolean =>
      !newValue.includes(item),
    );
    if (itemList.length > 0) {
      const store = as(this.getStore(), TreeStore);
      store.beginUpdate();
      itemList.forEach((rem: any): void => {
        const node = as(store.getById(rem.data.id), FolderTreeNode);
        if (node) {
          node.set("checked", false);
        }
      });
      store.endUpdate();
    }
  }

  #handleModelChange(changed: PropertyChangeEvent): void {
    const csm: ContentSyncSettings = this.modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING);
    const selectedValue: string = changed.newValue;
    if (!selectedValue) {
      return;
    }
    //if (!treeMap.containsKey(selectedValue)) {
    const tree: TreeStore = new FolderTreeStore(this, new ContentSyncIngestTreeModel(changed.newValue, csm.brandFolderId, this.modelBean), null, null);
    this.#treeMap.add(selectedValue, tree);
    //}
    this.modelBean.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, []);
    this.setStore(as(this.#treeMap.get(selectedValue), TreeStore));
  }

  #onCheckChanged(ev: FolderTreeNode): void {
    const obj = ev.data;
    const isChecked: boolean = obj.checked;

    let allSelectedContents: Array<any> = this.modelBean.get(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY);
    if (isChecked) {
      allSelectedContents = [ev].concat(allSelectedContents);
      //calculate the direct references, and the configured recursion depth.
      this.#resolveAndAddReferences(ev);
      this.modelBean.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, allSelectedContents);
    } else {
      let nonLeafchildNodes: Array<any> = ev.childNodes || [];
      nonLeafchildNodes = nonLeafchildNodes.concat(ev);
      ContentSyncHelper.synchronizeContentList(this.modelBean, nonLeafchildNodes);
    }

  }

  #resolveAndAddReferences(parentFolderTreeNode: FolderTreeNode): void {
    const contentSyncSetting: ContentSyncSettings = this.modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING);
    ContentSyncHelper.getReferencesFor(contentSyncSetting.identifier,
      parentFolderTreeNode.data.id,
      contentSyncSetting.recursionPartialSync,
      this.modelBean,
    )
      .then((item: ContentSyncReferenceModel): void => {
        const ref = item.getReferences(contentSyncSetting.identifier, this.modelBean);
        const processesReferences = [];
        ref.forEach((it: ContentSyncModel): void =>
          it.load((csm: ContentSyncModel): void => {
            const node = ContentSyncHelper.contentSyncModel2FolderTreeNode(csm, parentFolderTreeNode);
            processesReferences.push(node);
            const existentContent: Array<any> = this.modelBean.get(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY);
            this.modelBean.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, [node].concat(existentContent));
          }),
        );
        parentFolderTreeNode.childNodes = processesReferences;
      });
  }

  static #addCheckBox(parent: any, node: any): void {
    if (parseInt(node.data.id) % 2 == 0) {
      node.data.checked = false;
    }
  }

  override destroy(...params): void {
    super.destroy(params);
    //unregister the append event
    this.un(ContentSyncSourceTreePanelBase.#ITEM_APPEND, ContentSyncSourceTreePanelBase.#addCheckBox);
    this.un(ContentSyncSourceTreePanelBase.#CHECK_CHANGE, bind(this, this.#onCheckChanged));
    this.modelBean.removePropertyChangeListener(ContentSyncConstants.SELECTED_ENVIRONMENT, bind(this, this.#handleModelChange));
    this.modelBean.removePropertyChangeListener(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, bind(this, this.#handleContentListChange));
    this.modelBean.removePropertyChangeListener(ExcludeListRadioGroupBase.CONTENT_TYPE_EXCLUDE, bind(this, this.#handleExcludes));
    this.modelBean.removePropertyChangeListener(ExcludeListRadioGroupBase.PROPERTY_EXCLUDE, bind(this, this.#handleExcludes));
  }
}

export default ContentSyncSourceTreePanelBase;
