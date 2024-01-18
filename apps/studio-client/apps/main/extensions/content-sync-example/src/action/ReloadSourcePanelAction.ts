import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Action from "@jangaroo/ext-ts/Action";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import trace from "@jangaroo/runtime/trace";
import ContentSyncConstants from "../constant/ContentSyncConstants";

interface ReloadSourcePanelActionConfig extends Config<Action>, Partial<Pick<ReloadSourcePanelAction,
  "modelBean"
>> {
}

class ReloadSourcePanelAction extends Action {
  declare Config: ReloadSourcePanelActionConfig;

  #modelBean: Bean = null;

  get modelBean(): Bean {
    return this.#modelBean;
  }

  set modelBean(value: Bean) {
    this.#modelBean = value;
  }

  constructor(config: Config<ReloadSourcePanelAction> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    config.handler = bind(this$, this$.#handleReloadAction);
    super(config);
    this.modelBean = config.modelBean;
  }

  #handleReloadAction() {
    trace("[ReloadSourcePanelAction] reloading source tree");
    const setting: string = this.modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT);

    this.modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT, null);
    this.modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT, setting);

  }

}

export default ReloadSourcePanelAction;
