import Config from "@jangaroo/runtime/Config";
import { asConfig, bind } from "@jangaroo/runtime";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Action from "@jangaroo/ext-ts/Action";
interface TriggerWorkflowActionConfig extends Config<Action>, Partial<Pick<TriggerWorkflowAction,
  "modelBean"
>> {
}



class TriggerWorkflowAction extends Action{
  declare Config: TriggerWorkflowActionConfig;

  #modelBean:Bean = null
  ;
  get modelBean():Bean { return this.#modelBean; }
  set modelBean(value:Bean) { this.#modelBean = value; }
  constructor(config:Config<TriggerWorkflowAction> = null) {
    super((()=>{
    config.handler =bind( this,this.#triggerSelectedWorkflow);
    return config;})());
    this.modelBean = config.modelBean;
  }

  #triggerSelectedWorkflow(){
    ContentSyncHelper.startWorkflow(this.modelBean);
  }
}
export default TriggerWorkflowAction;
