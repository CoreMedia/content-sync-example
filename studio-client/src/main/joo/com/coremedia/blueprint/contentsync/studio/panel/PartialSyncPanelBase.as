package com.coremedia.blueprint.contentsync.studio.panel {
import com.coremedia.ui.data.Bean;

import ext.panel.Panel;

public class PartialSyncPanelBase extends Panel{

  [Bindable]
  public var modelBean:Bean;

  public function PartialSyncPanelBase(config:PartialSyncPanel = null) {
    super(config);
    modelBean = config.modelBean;
  }
}
}
