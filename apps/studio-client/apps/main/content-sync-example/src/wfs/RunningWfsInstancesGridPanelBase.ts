import Config from "@jangaroo/runtime/Config";
import {bind} from "@jangaroo/runtime";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import RunningWfsInstancesGridPanel from "./RunningWfsInstancesGridPanel";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import clearInterval from "@jangaroo/jooflash-core/flash/utils/clearInterval";
import setInterval from "@jangaroo/jooflash-core/flash/utils/setInterval";
import uint from "@jangaroo/runtime/uint";

interface RunningWfsInstancesGridPanelBaseConfig extends Config<GridPanel> {
}


class RunningWfsInstancesGridPanelBase extends GridPanel {
  declare Config: RunningWfsInstancesGridPanelBaseConfig;

  #runningVE: ValueExpression = null;
  #interval: uint = 0;

  constructor(config: Config<RunningWfsInstancesGridPanel> = null) {
    super(config);
    this.#checkRunningWorkflows();
  }

  override destroy(...params): void {
    super.destroy(params);
    clearInterval(this.#interval);
  }

  protected getVE(): ValueExpression {
    if (!this.#runningVE) {
      this.#runningVE = ValueExpressionFactory.createFromValue([]);
      this.#interval = setInterval(bind(this, this.#checkRunningWorkflows), 5000);
    }
    return this.#runningVE;
  }

  #checkRunningWorkflows(): void {
    ContentSyncHelper.getRunningInstances().then((running: Array<any>): void => {
      this.getVE().setValue(running);
    });
  }

  protected isComponentVisible(data: Array<any>): boolean {
    return data.length > 0;
  }

  protected handleAbortWorkflow(grid: GridPanel, rowIndex: number): void {
    var record = grid.getStore().getAt(rowIndex);
    ContentSyncHelper.abortWorkflow(record.data.id);
    var wfsInstances: Array<any> = this.getVE().getValue();
    this.getVE().setValue(wfsInstances.map((wf) =>
            wf.id === record.data.id
    ));
    EventUtil.invokeLater(bind(this, this.#checkRunningWorkflows));
  }

}

export default RunningWfsInstancesGridPanelBase;
