import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import RadioGroup from "@jangaroo/ext-ts/form/RadioGroup";
import Radio from "@jangaroo/ext-ts/form/field/Radio";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ContentSyncConstants from "../constant/ContentSyncConstants";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import ContentSyncSettings from "../model/ContentSyncSettings";
import SettingsAwareRadioGroup from "./SettingsAwareRadioGroup";

interface SettingsAwareRadioGroupBaseConfig extends Config<RadioGroup>, Partial<Pick<SettingsAwareRadioGroupBase,
  "modelBean" |
  "bindTo"
>> {
}

/**
 * Simple RadioGroup which is reading the:
 * - ContentSyncSettings document,
 * and is populating the:
 * - name entry as boxLabel
 * - ident as inputValue
 *
 * Afterwards the selected value is written into the model.
 */
class SettingsAwareRadioGroupBase extends RadioGroup {
  declare Config: SettingsAwareRadioGroupBaseConfig;

  #modelBean: Bean = null;

  get modelBean(): Bean {
    return this.#modelBean;
  }

  set modelBean(value: Bean) {
    this.#modelBean = value;
  }

  #bindTo: ConfigBasedValueExpression = null;

  get bindTo(): ConfigBasedValueExpression {
    return this.#bindTo;
  }

  set bindTo(value: ConfigBasedValueExpression) {
    this.#bindTo = value;
  }

  static readonly #CHANGE_EVENT: string = "change";

  static readonly #ENVIRONMENT: string = "environment";

  #settings: Array<any> = null;

  constructor(config: Config<SettingsAwareRadioGroup> = null) {
    super(config);
    this.modelBean = config.modelBean;
    this.bindTo = config.bindTo;
    this.addListener(SettingsAwareRadioGroupBase.#CHANGE_EVENT, bind(this, this.#handleChange));
  }

  #handleChange(changed: any): void {
    ContentSyncHelper.getContentSyncSettings().then((items: Array<any>): void => {
      const env: string = changed.getValue().environment;
      items.forEach((setting: ContentSyncSettings) =>{
        if (setting.identifier === env) {
          this.modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING, setting);
          this.bindTo.setValue(changed.getValue().environment);
        }
      });
    });
  }

  protected override afterRender(): void {
    super.afterRender();
    ContentSyncHelper.getContentSyncSettings().then((items: Array<any>): void =>
      items.forEach((setting: ContentSyncSettings, idx: number): void => {
        if (idx == 0) {
          this.modelBean.set(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING, setting);
          this.bindTo.setValue(setting.identifier);
        }
        this.add(new Radio(Config(Radio, {
          inputValue: setting.identifier,
          boxLabel: setting.name,
          name: SettingsAwareRadioGroupBase.#ENVIRONMENT,
          checked: idx == 0,
        })));
      }),
    );
  }
}

export default SettingsAwareRadioGroupBase;
