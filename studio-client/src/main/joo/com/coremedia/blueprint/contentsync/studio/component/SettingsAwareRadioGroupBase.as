package com.coremedia.blueprint.contentsync.studio.component {
import com.coremedia.blueprint.contentsync.studio.constant.ContentSyncConstants;
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncSettings;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.exml.ValueExpression;

import ext.form.RadioGroup;
import ext.form.field.Radio;

/**
 * Simple RadioGroup which is reading the:
 * - ContentSyncSettings document,
 * and is populating the:
 * - name entry as boxLabel
 * - ident as inputValue
 *
 * Afterwards the selected value is written into the model.
 */
public class SettingsAwareRadioGroupBase extends RadioGroup {

  [Bindable]
  public var modelBean:Bean;

  [Bindable]
  public var bindTo:ValueExpression;

  private static const CHANGE_EVENT:String = "change";
  private static const ENVIRONMENT:String = "environment";
  private var settings:Array;

  public function SettingsAwareRadioGroupBase(config:SettingsAwareRadioGroup = null) {
    super(config);
    modelBean = config.modelBean;
    bindTo = config.bindTo;
    addListener(CHANGE_EVENT, handleChange);
  }

  private function handleChange(changed:*):void {
    ContentSyncHelper.getContentSyncSettings().then(function (items:Array):void {
      var env:String = changed.getValue().environment;
      items.forEach(function (setting:ContentSyncSettings){
        if (setting.identifier === env){
          modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING,setting);
          bindTo.setValue(changed.getValue().environment);
        }
      });
    });
  }

  override protected function afterRender():void {
    super.afterRender();
    ContentSyncHelper.getContentSyncSettings().then(function (items:Array):void {
      items.forEach(function (setting:ContentSyncSettings,idx:Number):void {
        if (idx == 0){
          modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING,setting);
          bindTo.setValue(setting.identifier)
        }
        add(new Radio(Radio({
          inputValue: setting.identifier,
          boxLabel: setting.name,
          name: ENVIRONMENT,
          checked: idx == 0
        })));
      });
    });
  }
}
}
