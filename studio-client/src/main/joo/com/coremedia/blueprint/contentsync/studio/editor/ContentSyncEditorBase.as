package com.coremedia.blueprint.contentsync.studio.editor {
import com.coremedia.blueprint.contentsync.studio.constant.ContentSyncConstants;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.beanFactory;

import ext.panel.Panel;

public class ContentSyncEditorBase extends Panel {
  private var contentSyncModel:Bean;

  /*public static const CONTENT_LIST_BEAN_PROPERTY:String="contentList";*/
  public static const PARTIAL_SYNC_ID:String = "partialSync";
  public static const PARTIAL_SYNC_PANEL_ID:String = PARTIAL_SYNC_ID.concat('_panel');
  /*public static const SELECTED_SYNC_MODE:String ="selectedSyncMode";
  public static const SELECTED_ENVIRONMENT:String ="selectedEnvironment";
  public static const SWITCHING_CONTAINER_CONTROL_ID:String="switchingControlId";
  public static const SELECTED_ENVIRONMENT_SETTING:String = "selectedEnvironmentSetting";*/

  [Bindable]
  public var defaulSyncMode:String;

  public function ContentSyncEditorBase(config:ContentSyncEditor = null) {
    super(config);
    handleModelUpdate();
    getModel(config).addValueChangeListener(handleModelUpdate);
  }

  private function handleModelUpdate(){
    var bean:Bean = getModel(getInitialConfig());
    if (bean.get(ContentSyncConstants.SELECTED_SYNC_MODE)!==null && bean.get(ContentSyncConstants.SELECTED_ENVIRONMENT)!==null){
      bean.set(ContentSyncConstants.SWITCHING_CONTAINER_CONTROL_ID,bean.get(ContentSyncConstants.SELECTED_SYNC_MODE));
    }
  }


  public function getModel(config:ContentSyncEditor):Bean{
    if (!contentSyncModel){
      contentSyncModel = beanFactory.createLocalBean({
        selectedSyncMode: config.defaulSyncMode || PARTIAL_SYNC_ID,
        propertyExcludes:[],
        contentTypeExcludes:[],
        selectedEnvironment:undefined,
        switchingControlId:"",
        contentList:[]
      });
    }
    return contentSyncModel;
  }
}
}
