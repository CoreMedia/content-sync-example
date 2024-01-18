import HorizontalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HorizontalSpacingPlugin";
import CollapsiblePanel from "@coremedia/studio-client.main.editor-components/sdk/premular/CollapsiblePanel";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ContentSyncPluginResources_properties from "../ContentSyncPluginResources_properties";
import ContentSyncSourceTreePanel from "../tree/ContentSyncSourceTreePanel";
import ContentSyncTargetTreePanel from "../tree/ContentSyncTargetTreePanel";
import PartialSyncPanelBase from "./PartialSyncPanelBase";

interface PartialSyncPanelConfig extends Config<PartialSyncPanelBase> {
}

class PartialSyncPanel extends PartialSyncPanelBase {
  declare Config: PartialSyncPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.contentsync.studio.panel.partialSyncPanel";

  constructor(config: Config<PartialSyncPanel> = null) {
    super(ConfigUtils.apply(Config(PartialSyncPanel, {
      scrollable: true,

      items: [
        Config(CollapsiblePanel, {
          title: ContentSyncPluginResources_properties.ContentSync_PartialSyncPanel,
          collapsible: false,
          collapsed: false,
          items: [
            Config(ContentSyncSourceTreePanel, {
              modelBean: config.modelBean,
              flex: 2,
            }),
            Config(ContentSyncTargetTreePanel, {
              modelBean: config.modelBean,
              flex: 2,
            }),
          ],
          layout: Config(HBoxLayout),
          plugins: [
            Config(HorizontalSpacingPlugin),
          ],
        }),
      ],
    }), config));
  }
}

export default PartialSyncPanel;
