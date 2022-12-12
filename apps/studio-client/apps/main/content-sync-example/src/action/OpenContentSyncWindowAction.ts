import Config from "@jangaroo/runtime/Config";
import OpenContentSyncWindowActionBase from "./OpenContentSyncWindowActionBase";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
interface OpenContentSyncWindowActionConfig extends Config<OpenContentSyncWindowActionBase> {
}


class OpenContentSyncWindowAction extends OpenContentSyncWindowActionBase{
  declare Config: OpenContentSyncWindowActionConfig;

  constructor(config:Config<OpenContentSyncWindowAction> = null){
    super( ConfigUtils.apply(Config(OpenContentSyncWindowAction),config));
  }}
export default OpenContentSyncWindowAction;
