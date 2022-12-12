import Config from "@jangaroo/runtime/Config";
import ContentSyncStudioPlugin from "./ContentSyncStudioPlugin";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
interface ContentSyncStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class ContentSyncStudioPluginBase extends StudioPlugin {
  declare Config: ContentSyncStudioPluginBaseConfig;

  constructor(config:Config<ContentSyncStudioPlugin> = null) {
    super(config);
  }

  override init(editorContext:IEditorContext):void {
    super.init(editorContext);
  }
}
export default ContentSyncStudioPluginBase;
