import Config from "@jangaroo/runtime/Config";
import { as, asConfig, bind } from "@jangaroo/runtime";
import ContentSyncEditor from "../editor/ContentSyncEditor";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import WorkAreaTabType from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkAreaTabType";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import Action from "@jangaroo/ext-ts/Action";
import Panel from "@jangaroo/ext-ts/panel/Panel";
interface OpenContentSyncWindowActionBaseConfig extends Config<Action> {
}



/**
 * Simple action which is opening the ContentSyncEditor
 */
class OpenContentSyncWindowActionBase extends Action {
  declare Config: OpenContentSyncWindowActionBaseConfig;
  constructor(config:Config<OpenContentSyncWindowActionBase> = null) {
    super((()=>{
    config.handler =bind( this,this.#performOpenEditor);
    return config;})());
  }

  #performOpenEditor() {
    var workArea =as( editorContext._.getWorkArea(),  WorkArea);
    var contentSyncWindowTab =as( Ext.getCmp(ContentSyncEditor.CONTENT_SYNC_ID),  ContentSyncEditor);
    if (!contentSyncWindowTab) {

      var workAreaTabType = workArea.getTabTypeById(ContentSyncEditor.xtype);
      workAreaTabType.createTab(null, (tab:Panel):void => {
        var editor =as( tab,  ContentSyncEditor);
        workArea.addTab(workAreaTabType, editor);
        workArea.setActiveTab(editor);
      });
    } else {
      workArea.setActiveTab(ContentSyncEditor);
    }
  }
}
export default OpenContentSyncWindowActionBase;
