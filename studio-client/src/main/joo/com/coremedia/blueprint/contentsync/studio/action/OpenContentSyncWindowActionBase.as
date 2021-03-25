package com.coremedia.blueprint.contentsync.studio.action {
import com.coremedia.blueprint.contentsync.studio.editor.ContentSyncEditor;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.desktop.WorkAreaTabType;
import com.coremedia.cms.editor.sdk.editorContext;

import ext.Action;
import ext.Ext;
import ext.panel.Panel;

/**
 * Simple action which is opening the ContentSyncEditor
 */
public class OpenContentSyncWindowActionBase extends Action {
  public function OpenContentSyncWindowActionBase(config:OpenContentSyncWindowActionBase = null) {
    config.handler = performOpenEditor;
    super(config);
  }

  private function performOpenEditor() {
    var workArea:WorkArea = editorContext.getWorkArea() as WorkArea;
    var contentSyncWindowTab:ContentSyncEditor = Ext.getCmp(ContentSyncEditor.CONTENT_SYNC_ID) as ContentSyncEditor;
    if (!contentSyncWindowTab) {

      var workAreaTabType:WorkAreaTabType = workArea.getTabTypeById(ContentSyncEditor.xtype);
      workAreaTabType.createTab(null, function (tab:Panel):void {
        var editor:ContentSyncEditor = tab as ContentSyncEditor;
        workArea.addTab(workAreaTabType, editor);
        workArea.setActiveTab(editor);
      });
    } else {
      workArea.setActiveTab(ContentSyncEditor);
    }
  }
}
}
