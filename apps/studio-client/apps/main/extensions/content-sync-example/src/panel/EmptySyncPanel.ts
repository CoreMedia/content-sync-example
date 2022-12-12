import Config from "@jangaroo/runtime/Config";
import ContentSyncPluginResources_properties from "../ContentSyncPluginResources_properties";
import EmptySyncPanelBase from "./EmptySyncPanelBase";
import CollapsiblePanel from "@coremedia/studio-client.main.editor-components/sdk/premular/CollapsiblePanel";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
interface EmptySyncPanelConfig extends Config<EmptySyncPanelBase> {
}


class EmptySyncPanel extends EmptySyncPanelBase{
  declare Config: EmptySyncPanelConfig;

  static override readonly xtype:string = "com.coremedia.blueprint.contentsync.studio.panel.emptySyncPanel";

  constructor(config:Config<EmptySyncPanel> = null){
    super( ConfigUtils.apply(Config(EmptySyncPanel, {


  items:[

    Config(CollapsiblePanel, {
            title: ContentSyncPluginResources_properties.ContentSync_EmptySyncPanel_Name,
            collapsible: false,
            collapsed: false,
      items:[

      ]


    })
  ]
}),config));
  }}
export default EmptySyncPanel;
