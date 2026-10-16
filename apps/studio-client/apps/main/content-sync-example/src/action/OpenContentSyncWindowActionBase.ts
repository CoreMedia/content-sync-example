import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import Action from "@jangaroo/ext-ts/Action";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ContentSyncEditor from "../editor/ContentSyncEditor";

interface OpenContentSyncWindowActionBaseConfig extends Config<Action> {
}

/**
 * Simple action which is opening the ContentSyncEditor
 */
class OpenContentSyncWindowActionBase extends Action {
  declare Config: OpenContentSyncWindowActionBaseConfig;

  constructor(config: Config<OpenContentSyncWindowActionBase> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    config.handler = bind(this$, this$.#performOpenEditor);
    super(config);
  }

  #performOpenEditor() {
    const workArea = as(editorContext._.getWorkArea(), WorkArea);
    const contentSyncWindowTab = as(Ext.getCmp(ContentSyncEditor.CONTENT_SYNC_ID), ContentSyncEditor);
    if (!contentSyncWindowTab) {

      const workAreaTabType = workArea.getTabTypeById(ContentSyncEditor.xtype);
      workAreaTabType.createTab(null, (tab: Panel): void => {
        const editor = as(tab, ContentSyncEditor);
        workArea.addTab(workAreaTabType, editor);
        workArea.setActiveTab(editor);
      });
    } else {
      workArea.setActiveTab(ContentSyncEditor);
    }
  }
}

export default OpenContentSyncWindowActionBase;
