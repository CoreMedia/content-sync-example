import Config from "@jangaroo/runtime/Config";
import EmptySyncPanel from "./EmptySyncPanel";
import Panel from "@jangaroo/ext-ts/panel/Panel";
interface EmptySyncPanelBaseConfig extends Config<Panel> {
}



class EmptySyncPanelBase extends Panel{
  declare Config: EmptySyncPanelBaseConfig;
  constructor(config:Config<EmptySyncPanel> = null) {
    super(config);
  }
}
export default EmptySyncPanelBase;
