package com.coremedia.blueprint.contentsync.studio.action {
import com.coremedia.blueprint.contentsync.studio.constant.ContentSyncConstants;
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncSettings;
import com.coremedia.ui.data.Bean;

import ext.Action;

public class ReloadSourcePanelAction extends Action{

  [Bindable]
  public var modelBean:Bean;

  public function ReloadSourcePanelAction(config:ReloadSourcePanelAction = null) {
    config.handler = handleReloadAction;
    super(config);
    modelBean = config.modelBean;
  }

  private function handleReloadAction(){
    trace("[ReloadSourcePanelAction] reloading source tree");
    var setting:String = modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT);

    modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT,null);
    modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT,setting);


  }

}
}
