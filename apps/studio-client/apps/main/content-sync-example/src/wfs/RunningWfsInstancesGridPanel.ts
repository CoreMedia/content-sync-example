import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import LinkListBindListPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListBindListPlugin";
import ActionColumn from "@jangaroo/ext-ts/grid/column/Action";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ContentSyncPluginResources_properties from "../ContentSyncPluginResources_properties";
import RunningWfsInstancesGridPanelBase from "./RunningWfsInstancesGridPanelBase";

interface RunningWfsInstancesGridPanelConfig extends Config<RunningWfsInstancesGridPanelBase> {
}

class RunningWfsInstancesGridPanel extends RunningWfsInstancesGridPanelBase {
  declare Config: RunningWfsInstancesGridPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.contentsync.studio.wfs.runningWfsInstancesGridPanel";

  constructor(config: Config<RunningWfsInstancesGridPanel> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(RunningWfsInstancesGridPanel, {

      columns: [
        Config(Column, {
          stateId: "name",
          flex: 1,
          sortable: false,
          dataIndex: "name",
        }),
        Config(ActionColumn, {
          stateId: "id",
          iconCls: CoreIcons_properties.remove,
          sortable: false,
          flex: 1,
          tooltip: ContentSyncPluginResources_properties.ContentSync_AbortWorkflow_ToolTip,
          handler: bind(this$, this$.handleAbortWorkflow),
        }),
      ],
      plugins: [
        Config(LinkListBindListPlugin, { bindTo: this$.getVE() }),
        Config(BindVisibilityPlugin, {
          bindTo: this$.getVE(),
          transformer: bind(this$, this$.isComponentVisible),
        }),
      ],
      layout: Config(HBoxLayout, { align: "strech" }),
    }), config));
  }
}

export default RunningWfsInstancesGridPanel;
