import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ContentSyncTargetTreePanelBase from "./ContentSyncTargetTreePanelBase";

interface ContentSyncTargetTreePanelConfig extends Config<ContentSyncTargetTreePanelBase> {
}

class ContentSyncTargetTreePanel extends ContentSyncTargetTreePanelBase {
  declare Config: ContentSyncTargetTreePanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.contentsync.studio.tree.contentSyncTargetTreePanel";

  constructor(config: Config<ContentSyncTargetTreePanel> = null) {
    super(ConfigUtils.apply(Config(ContentSyncTargetTreePanel, {

      tbar: Config(Toolbar, {
        height: 20,
        ui: ToolbarSkin.DARK_GREY.getSkin(),
      }),

    }), config));
  }
}

export default ContentSyncTargetTreePanel;
