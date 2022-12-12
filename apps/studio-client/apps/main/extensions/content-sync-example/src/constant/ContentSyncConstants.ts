
class ContentSyncConstants {

    constructor(){
      throw new Error("can not instantiated");
    }

  static readonly CONTENT_LIST_BEAN_PROPERTY:string="contentList";
  static readonly PARTIAL_SYNC_ID:string = "partialSyncModeId";
  static readonly PARTIAL_SYNC_PANEL_ID:string = ContentSyncConstants.PARTIAL_SYNC_ID.concat("_panel");
  static readonly SELECTED_SYNC_MODE:string ="selectedSyncMode";
  static readonly SELECTED_ENVIRONMENT:string ="selectedEnvironment";
  static readonly SWITCHING_CONTAINER_CONTROL_ID:string="switchingControlId";
  static readonly SELECTED_ENVIRONMENT_SETTING:string = "selectedEnvironmentSetting";
  static readonly REMOVE_FROM_TREE:string = "removeFromTree";

}
export default ContentSyncConstants;
