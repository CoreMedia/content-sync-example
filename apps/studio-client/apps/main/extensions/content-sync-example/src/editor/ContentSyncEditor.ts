import Config from "@jangaroo/runtime/Config";
import ContentSyncPluginResources_properties from "../ContentSyncPluginResources_properties";
import ContentSyncConstants from "../constant/ContentSyncConstants";
import ContentSyncLeftContainer from "../container/ContentSyncLeftContainer";
import PartialSyncPanel from "../panel/PartialSyncPanel";
import ContentSyncEditorBase from "./ContentSyncEditorBase";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
interface ContentSyncEditorConfig extends Config<ContentSyncEditorBase> {
}


class ContentSyncEditor extends ContentSyncEditorBase{
  declare Config: ContentSyncEditorConfig;

  static readonly CONTENT_SYNC_ID:string = "contentSyncEditor";
  static override readonly xtype:string = "com.coremedia.blueprint.contentsync.studio.editor.contentSyncEditor";

  constructor(config:Config<ContentSyncEditor> = null){
    super((()=> ConfigUtils.apply(Config(ContentSyncEditor, {
        id: ContentSyncEditor.CONTENT_SYNC_ID,
        itemId:  ContentSyncEditor.CONTENT_SYNC_ID,
        title: ContentSyncPluginResources_properties.ContentSync_Editor_Name,

  layout: Config(HBoxLayout),
  items:[

    /* The switching container holding all panels. A panel is responsible to present synchronization options
    to the editor (in this example the PartialSyncPanel)

     */
    Config(ContentSyncLeftContainer, { modelBean: this.getModel(config)}),
    Config(SwitchingContainer, { flex: 6,
                           ui: ConfigUtils.asString( ContainerSkin.DARK_200),
                           height: "100%",
                           scrollable: true,
      items:[
        Config(PartialSyncPanel, { itemId:  ContentSyncEditorBase.PARTIAL_SYNC_PANEL_ID, modelBean: this.getModel(config)})
        /*contentsync:EmptySyncPanel itemId="emptyPanelId"/*/
        /*
          Add panel here for more synchronisation modes
         */
      ],
      activeItemValueExpression: new ConfigBasedValueExpression({ context: this.getModel(config), expression:  ContentSyncConstants.SWITCHING_CONTAINER_CONTROL_ID
      })
    })
  ]

}),config))());
  }}
export default ContentSyncEditor;
