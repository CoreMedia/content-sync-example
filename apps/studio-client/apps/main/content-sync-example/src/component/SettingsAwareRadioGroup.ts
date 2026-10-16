import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ContentSyncConstants from "../constant/ContentSyncConstants";
import SettingsAwareRadioGroupBase from "./SettingsAwareRadioGroupBase";

interface SettingsAwareRadioGroupConfig extends Config<SettingsAwareRadioGroupBase> {
}

class SettingsAwareRadioGroup extends SettingsAwareRadioGroupBase {
  declare Config: SettingsAwareRadioGroupConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.contentsync.studio.component.SettingsAwareRadioGroup";

  constructor(config: Config<SettingsAwareRadioGroup> = null) {
    super(ConfigUtils.apply(Config(SettingsAwareRadioGroup, {

      items: [
      ],
      bindTo: new ConfigBasedValueExpression({
        context: config.modelBean,
        expression: ContentSyncConstants.SELECTED_ENVIRONMENT,
      }),
    }), config));
  }
}

export default SettingsAwareRadioGroup;
