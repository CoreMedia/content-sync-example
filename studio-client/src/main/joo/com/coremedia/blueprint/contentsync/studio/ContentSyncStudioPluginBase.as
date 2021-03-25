package com.coremedia.blueprint.contentsync.studio {
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;

public class ContentSyncStudioPluginBase extends StudioPlugin {

  public function ContentSyncStudioPluginBase(config:ContentSyncStudioPlugin = null) {
    super(config);
  }

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);
  }
}

}
