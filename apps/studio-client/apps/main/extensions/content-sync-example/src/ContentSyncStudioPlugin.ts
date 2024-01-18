import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import RegisterRestResource from "@coremedia/studio-client.main.editor-components/configuration/RegisterRestResource";
import ComponentBasedWorkAreaTabType from "@coremedia/studio-client.main.editor-components/sdk/desktop/ComponentBasedWorkAreaTabType";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import WorkAreaTabTypesPlugin from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkAreaTabTypesPlugin";
import Button from "@jangaroo/ext-ts/button/Button";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ContentSyncPluginResources_properties from "./ContentSyncPluginResources_properties";
import ContentSyncStudioPluginBase from "./ContentSyncStudioPluginBase";
import OpenContentSyncWindowAction from "./action/OpenContentSyncWindowAction";
import ContentSyncEditor from "./editor/ContentSyncEditor";
import StudioAppsImpl from "@coremedia/studio-client.app-context-models/apps/StudioAppsImpl";
import studioApps from "@coremedia/studio-client.app-context-models/apps/studioApps";
import ContentSyncModel from "./model/ContentSyncModel";
import ContentSyncReferenceModel from "./model/ContentSyncReferenceModel";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import {cast} from "@jangaroo/runtime";

interface ContentSyncStudioPluginConfig extends Config<ContentSyncStudioPluginBase> {
}

class ContentSyncStudioPlugin extends ContentSyncStudioPluginBase {
  declare Config: ContentSyncStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.contentsync.studio.contentSyncStudioPlugin";

  static readonly CONTENT_SYNC_EDITOR_BUTTON_ID: string = "ContentSyncButtonId";

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    const buttonCfg =Config(Button, {
      id: ContentSyncStudioPlugin.CONTENT_SYNC_EDITOR_BUTTON_ID,
      itemId: ContentSyncStudioPlugin.CONTENT_SYNC_EDITOR_BUTTON_ID,
      text: ContentSyncPluginResources_properties.ContentSync_MenuToolbar_Button_Name,
      ui: ButtonSkin.HIGHLIGHT.getSkin(),
      scale: "medium",
      iconCls: ContentSyncPluginResources_properties.ContentSync_icon,
      baseAction: new OpenContentSyncWindowAction({}),
    });
    buttonCfg.ariaLabel = "ignored";

    const button = new Button(buttonCfg);

    cast(StudioAppsImpl, studioApps._).getSubAppLauncherRegistry().registerSubAppLauncher("ContentSync", (): void => {
      typeof button.handler !== "string" && button.handler(button, null);
    });
  }

  constructor(config: Config<ContentSyncStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(ContentSyncStudioPlugin, {

      rules: [
        Config(WorkArea, {
          plugins: [
            Config(WorkAreaTabTypesPlugin, {
              tabTypes: [
                new ComponentBasedWorkAreaTabType({
                  tabComponent: Config(ContentSyncEditor, {
                    closable: true,
                    scrollable: true,
                  }),
                }),
              ],
            }),
          ],
        }),
      ],
      configuration: [
        new RegisterRestResource({ beanClass: ContentSyncReferenceModel }),
        new RegisterRestResource({ beanClass: ContentSyncModel }),

      ],
    }), config));
  }
}

export default ContentSyncStudioPlugin;
