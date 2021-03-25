package com.coremedia.blueprint.contentsync.studio.action {
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.ui.data.Bean;

import ext.Action;

public class TriggerWorkflowAction extends Action{
  [Bindable]
  public var modelBean:Bean
  ;
  public function TriggerWorkflowAction(config:TriggerWorkflowAction = null) {
    config.handler = triggerSelectedWorkflow;
    super(config);
    modelBean = config.modelBean;
  }

  private function triggerSelectedWorkflow(){
    ContentSyncHelper.startWorkflow(modelBean);
  }
}
}
