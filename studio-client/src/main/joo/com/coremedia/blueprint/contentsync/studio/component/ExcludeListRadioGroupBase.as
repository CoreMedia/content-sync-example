package com.coremedia.blueprint.contentsync.studio.component {
import com.coremedia.blueprint.contentsync.studio.constant.ContentSyncConstants;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncSettings;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;

import ext.form.field.Checkbox;
import ext.panel.Panel;

/**
 * Exclude / filtering functionality for:
 * - Properties
 * - ContentTypes
 *
 * please note that the configuration for the property and contentType exclude need to be configured
 * in the setting document.
 */
public class ExcludeListRadioGroupBase extends Panel {

  public static const PROPERTY_EXCLUDE:String = "propertyExcludes";
  public static const CONTENT_TYPE_EXCLUDE:String = "contentTypeExcludes";

  [Bindable]
  public var modelBean:Bean;

  [Bindable]
  public var modelProperty:String;

  public function ExcludeListRadioGroupBase(config:ExcludeListRadioGroup = null) {
    super(config);
    modelBean = config.modelBean;
    modelBean.addPropertyChangeListener(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING, handleSettingChange);
    var setting:ContentSyncSettings = modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING) as ContentSyncSettings;
    if (setting) {
      handleSetting(setting);
    }
  }

  private function handleSetting(setting:ContentSyncSettings):void {
    if (!setting){
      return;
    }
    var items:Array;
    if (modelProperty === PROPERTY_EXCLUDE) {
      items = setting.propertyExcludes;
    } else if (modelProperty === CONTENT_TYPE_EXCLUDE) {
      items = setting.contentTypeExcludes;
    }
    if (items) {
      items.forEach(function (item:String):void {
        var checkBox:Checkbox = new Checkbox(Checkbox({
          boxLabel: item,
          name: item,
          itemId: item.concat("_checkbox" + modelProperty),
          listeners: {
            change: {
              fn: handleChange
            }
          }
        }));
        add(checkBox);
      });
    }
  }

  private function handleSettingChange(setting:PropertyChangeEvent):void {
    removeAll(true);
    handleSetting(setting.newValue);
  }

  private function handleChange(changed:*):void {
    var excludes:Array = modelBean.get(modelProperty);
    var checkBoxName:String = changed.name;
    if (changed.checked) {
      modelBean.set(modelProperty, [checkBoxName].concat(excludes));
    } else {
      var clearedList:* = excludes.filter(function (it:*):Boolean {
        return it !== checkBoxName;
      });
      modelBean.set(modelProperty, clearedList);
    }
  }

}
}
