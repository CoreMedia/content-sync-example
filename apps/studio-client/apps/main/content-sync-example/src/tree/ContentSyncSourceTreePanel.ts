import Config from "@jangaroo/runtime/Config";
import ReloadSourcePanelAction from "../action/ReloadSourcePanelAction";
import ContentSyncSourceTreePanelBase from "./ContentSyncSourceTreePanelBase";
import EncodingUtil from "@coremedia/studio-client.client-core/util/EncodingUtil";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import TreeColumn from "@jangaroo/ext-ts/tree/Column";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface ContentSyncSourceTreePanelConfig extends Config<ContentSyncSourceTreePanelBase> {
}


class ContentSyncSourceTreePanel extends ContentSyncSourceTreePanelBase {
  declare Config: ContentSyncSourceTreePanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.contentsync.studio.contentSyncSourceTreePanel";

  constructor(config: Config<ContentSyncSourceTreePanel> = null) {
    super(ConfigUtils.apply(Config(ContentSyncSourceTreePanel, {

      tbar: Config(Toolbar, {
        height: 24,
        ui: ToolbarSkin.DARK_GREY.getSkin(),
        items: [
          Config(IconButton, {
            iconCls: CoreIcons_properties.reload,
            baseAction: new ReloadSourcePanelAction({
              modelBean: config.modelBean
            })
          })
        ]
      }),
      columns: [
        Config(TreeColumn, {
          dataIndex: "text",
          flex: 1,
          align: "left",
          renderer: EncodingUtil.encodeForHTML
        })
      ]
    }), config));
  }
}

export default ContentSyncSourceTreePanel;
