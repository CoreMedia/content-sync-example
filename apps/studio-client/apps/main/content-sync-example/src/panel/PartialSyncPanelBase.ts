import Config from "@jangaroo/runtime/Config";
import { asConfig } from "@jangaroo/runtime";
import PartialSyncPanel from "./PartialSyncPanel";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Panel from "@jangaroo/ext-ts/panel/Panel";
interface PartialSyncPanelBaseConfig extends Config<Panel>, Partial<Pick<PartialSyncPanelBase,
  "modelBean"
>> {
}



class PartialSyncPanelBase extends Panel{
  declare Config: PartialSyncPanelBaseConfig;

  #modelBean:Bean = null;

  get modelBean():Bean { return this.#modelBean; }
  set modelBean(value:Bean) { this.#modelBean = value; }

  constructor(config:Config<PartialSyncPanel> = null) {
    super(config);
    this.modelBean = config.modelBean;
  }
}
export default PartialSyncPanelBase;
