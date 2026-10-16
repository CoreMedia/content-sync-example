import Bean from "@coremedia/studio-client.client-core/data/Bean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ContentSyncConstants from "../constant/ContentSyncConstants";
import ContentSyncEditor from "./ContentSyncEditor";

interface ContentSyncEditorBaseConfig extends Config<Panel>, Partial<Pick<ContentSyncEditorBase,
  "defaulSyncMode"
>> {
}

class ContentSyncEditorBase extends Panel {
  declare Config: ContentSyncEditorBaseConfig;

  #contentSyncModel: Bean = null;

  /*public static const CONTENT_LIST_BEAN_PROPERTY:String="contentList";*/
  static readonly PARTIAL_SYNC_ID: string = "partialSync";

  static readonly PARTIAL_SYNC_PANEL_ID: string = ContentSyncEditorBase.PARTIAL_SYNC_ID.concat("_panel");

  #defaulSyncMode: string = null;
  /*public static const SELECTED_SYNC_MODE:String ="selectedSyncMode";
  public static const SELECTED_ENVIRONMENT:String ="selectedEnvironment";
  public static const SWITCHING_CONTAINER_CONTROL_ID:String="switchingControlId";
  public static const SELECTED_ENVIRONMENT_SETTING:String = "selectedEnvironmentSetting";*/

  get defaulSyncMode(): string {
    return this.#defaulSyncMode;
  }

  set defaulSyncMode(value: string) {
    this.#defaulSyncMode = value;
  }

  constructor(config: Config<ContentSyncEditor> = null) {
    super(config);
    this.#handleModelUpdate();
    this.getModel(config).addValueChangeListener(bind(this, this.#handleModelUpdate));
  }

  #handleModelUpdate() {
    const bean = this.getModel(this.getInitialConfig());
    if (bean.get(ContentSyncConstants.SELECTED_SYNC_MODE) !== null && bean.get(ContentSyncConstants.SELECTED_ENVIRONMENT) !== null) {
      bean.set(ContentSyncConstants.SWITCHING_CONTAINER_CONTROL_ID, bean.get(ContentSyncConstants.SELECTED_SYNC_MODE));
    }
  }

  getModel(config: Config<ContentSyncEditor>): Bean {
    if (!this.#contentSyncModel) {
      this.#contentSyncModel = beanFactory._.createLocalBean({
        selectedSyncMode: config.defaulSyncMode || ContentSyncEditorBase.PARTIAL_SYNC_ID,
        propertyExcludes: [],
        contentTypeExcludes: [],
        selectedEnvironment: undefined,
        switchingControlId: "",
        contentList: [],
      });
    }
    return this.#contentSyncModel;
  }
}

export default ContentSyncEditorBase;
