package com.coremedia.blueprint.contentsync.studio.wfs {
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.EventUtil;

import ext.data.Model;
import ext.grid.GridPanel;

import flash.utils.clearInterval;
import flash.utils.setInterval;

public class RunningWfsInstancesGridPanelBase extends GridPanel {

  private var runningVE:ValueExpression;
  private var interval:uint;

  public function RunningWfsInstancesGridPanelBase(config:RunningWfsInstancesGridPanel = null) {
    super(config);
    checkRunningWorkflows();
  }

  override public function destroy(...params):void {
    super.destroy(params);
    clearInterval(interval);
  }

  protected function getVE():ValueExpression {
    if (!runningVE) {
      runningVE = ValueExpressionFactory.createFromValue([]);
      interval = setInterval(checkRunningWorkflows, 5000);
    }
    return runningVE;
  }

  private function checkRunningWorkflows():void {
    ContentSyncHelper.getRunningInstances().then(function (running:Array):void {
      getVE().setValue(running);
    });
  }

  protected function isComponentVisible(data:Array):Boolean {
    return data.length > 0
  }

  protected function handleAbortWorkflow(grid:GridPanel, rowIndex:Number):void {
    var record:Model = grid.getStore().getAt(rowIndex);
    ContentSyncHelper.abortWorkflow(record.data.id);
    var wfsInstances:Array = getVE().getValue();
    getVE().setValue(wfsInstances.map(function (wf){
      return wf.id === record.data.id;
    }));
    EventUtil.invokeLater(checkRunningWorkflows);
  }

}
}
