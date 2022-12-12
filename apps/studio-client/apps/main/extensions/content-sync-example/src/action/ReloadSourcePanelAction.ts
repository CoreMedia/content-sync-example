import Config from "@jangaroo/runtime/Config";
import { asConfig, bind } from "@jangaroo/runtime";
import ContentSyncConstants from "../constant/ContentSyncConstants";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Action from "@jangaroo/ext-ts/Action";
import trace from "@jangaroo/runtime/trace";
interface ReloadSourcePanelActionConfig extends Config<Action>, Partial<Pick<ReloadSourcePanelAction,
  "modelBean"
>> {
}



class ReloadSourcePanelAction extends Action{
  declare Config: ReloadSourcePanelActionConfig;

  #modelBean:Bean = null;

  get modelBean():Bean { return this.#modelBean; }
  set modelBean(value:Bean) { this.#modelBean = value; }

  constructor(config:Config<ReloadSourcePanelAction> = null) {
    super((()=>{
    config.handler =bind( this,this.#handleReloadAction);
    return config;})());
    this.modelBean = config.modelBean;
  }

  #handleReloadAction(){
    trace("[ReloadSourcePanelAction] reloading source tree");
    var setting:string = this.modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT);

    this.modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT,null);
    this.modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT,setting);


  }

}
export default ReloadSourcePanelAction;
