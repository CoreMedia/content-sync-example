import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Config from "@jangaroo/runtime/Config";
import PartialSyncPanel from "./PartialSyncPanel";

interface PartialSyncPanelBaseConfig extends Config<Panel>, Partial<Pick<PartialSyncPanelBase,
  "modelBean"
>> {
}

class PartialSyncPanelBase extends Panel {
  declare Config: PartialSyncPanelBaseConfig;

  #modelBean: Bean = null;

  get modelBean(): Bean {
    return this.#modelBean;
  }

  set modelBean(value: Bean) {
    this.#modelBean = value;
  }

  constructor(config: Config<PartialSyncPanel> = null) {
    super(config);
    this.modelBean = config.modelBean;
  }
}

export default PartialSyncPanelBase;
