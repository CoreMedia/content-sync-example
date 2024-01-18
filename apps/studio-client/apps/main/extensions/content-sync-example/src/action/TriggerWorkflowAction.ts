import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Action from "@jangaroo/ext-ts/Action";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ContentSyncHelper from "../helper/ContentSyncHelper";

interface TriggerWorkflowActionConfig extends Config<Action>, Partial<Pick<TriggerWorkflowAction,
  "modelBean"
>> {
}

class TriggerWorkflowAction extends Action {
  declare Config: TriggerWorkflowActionConfig;

  #modelBean: Bean = null
  ;

  get modelBean(): Bean {
    return this.#modelBean;
  }

  set modelBean(value: Bean) {
    this.#modelBean = value;
  }

  constructor(config: Config<TriggerWorkflowAction> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    config.handler = bind(this$, this$.#triggerSelectedWorkflow);
    super(config);
    this.modelBean = config.modelBean;
  }

  #triggerSelectedWorkflow() {
    ContentSyncHelper.startWorkflow(this.modelBean);
  }
}

export default TriggerWorkflowAction;
