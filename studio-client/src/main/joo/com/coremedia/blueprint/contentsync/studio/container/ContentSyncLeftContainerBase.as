package com.coremedia.blueprint.contentsync.studio.container {
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncSettings;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.container.Container;

public class ContentSyncLeftContainerBase extends Container {

  private var excludePropertiesHiddenVE:ValueExpression;
  private var excludeContentTypeHiddenVE:ValueExpression;

  public function ContentSyncLeftContainerBase(config:ContentSyncLeftContainer = null) {
    super(config);
    ContentSyncHelper.getContentSyncSettings().then(function (item:Array):void {
      if (item.length > 0) {
        var setting:ContentSyncSettings = item[0];
        getExcludePropertiesVE().setValue(setting.propertyExcludes);
        getExcludeContentTypeVE().setValue(setting.contentTypeExcludes)
      }
    });
  }

  public function getExcludePropertiesVE():ValueExpression {
    if (!excludePropertiesHiddenVE) {
      excludePropertiesHiddenVE = ValueExpressionFactory.createFromValue([]);
    }
    return excludePropertiesHiddenVE;
  }

  public function getExcludeContentTypeVE():ValueExpression {
    if (!excludeContentTypeHiddenVE) {
      excludeContentTypeHiddenVE = ValueExpressionFactory.createFromValue([]);
    }
    return excludeContentTypeHiddenVE;
  }

  protected function hasEntries(data:Array):Boolean {
    return data.length > 0;
  }

}
}
