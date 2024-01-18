import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenContentSyncWindowActionBase from "./OpenContentSyncWindowActionBase";

interface OpenContentSyncWindowActionConfig extends Config<OpenContentSyncWindowActionBase> {
}

class OpenContentSyncWindowAction extends OpenContentSyncWindowActionBase {
  declare Config: OpenContentSyncWindowActionConfig;

  constructor(config: Config<OpenContentSyncWindowAction> = null) {
    super(ConfigUtils.apply(Config(OpenContentSyncWindowAction), config));
  }
}

export default OpenContentSyncWindowAction;
