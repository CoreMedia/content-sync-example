package com.coremedia.blueprint.contentsync.studio.constant {
public class ContentSyncConstants {

    function ContentSyncConstants(){
      throw new Error("can not instantiated");
    }

  public static const CONTENT_LIST_BEAN_PROPERTY:String="contentList";
  public static const PARTIAL_SYNC_ID:String = "partialSyncModeId";
  public static const PARTIAL_SYNC_PANEL_ID:String = PARTIAL_SYNC_ID.concat('_panel');
  public static const SELECTED_SYNC_MODE:String ="selectedSyncMode";
  public static const SELECTED_ENVIRONMENT:String ="selectedEnvironment";
  public static const SWITCHING_CONTAINER_CONTROL_ID:String="switchingControlId";
  public static const SELECTED_ENVIRONMENT_SETTING:String = "selectedEnvironmentSetting";
  public static const REMOVE_FROM_TREE:String = "removeFromTree";

}
}
