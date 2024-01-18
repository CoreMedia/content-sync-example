import Bean from "@coremedia/studio-client.client-core/data/Bean";
import PropertyChangeEvent from "@coremedia/studio-client.client-core/data/PropertyChangeEvent";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ContentSyncConstants from "../constant/ContentSyncConstants";
import ContentSyncSettings from "../model/ContentSyncSettings";
import ExcludeListRadioGroup from "./ExcludeListRadioGroup";

interface ExcludeListRadioGroupBaseConfig extends Config<Panel>, Partial<Pick<ExcludeListRadioGroupBase,
  "modelBean" |
  "modelProperty"
>> {
}

/**
 * Exclude / filtering functionality for:
 * - Properties
 * - ContentTypes
 *
 * please note that the configuration for the property and contentType exclude need to be configured
 * in the setting document.
 */
class ExcludeListRadioGroupBase extends Panel {
  declare Config: ExcludeListRadioGroupBaseConfig;

  static readonly PROPERTY_EXCLUDE: string = "propertyExcludes";

  static readonly CONTENT_TYPE_EXCLUDE: string = "contentTypeExcludes";

  #modelBean: Bean = null;

  get modelBean(): Bean {
    return this.#modelBean;
  }

  set modelBean(value: Bean) {
    this.#modelBean = value;
  }

  #modelProperty: string = null;

  get modelProperty(): string {
    return this.#modelProperty;
  }

  set modelProperty(value: string) {
    this.#modelProperty = value;
  }

  constructor(config: Config<ExcludeListRadioGroup> = null) {
    super(config);
    this.modelBean = config.modelBean;
    this.modelBean.addPropertyChangeListener(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING, bind(this, this.#handleSettingChange));
    const setting = as(this.modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING), ContentSyncSettings);
    if (setting) {
      this.#handleSetting(setting);
    }
  }

  #handleSetting(setting: ContentSyncSettings): void {
    if (!setting) {
      return;
    }
    let items: Array<any>;
    if (this.modelProperty === ExcludeListRadioGroupBase.PROPERTY_EXCLUDE) {
      items = setting.propertyExcludes;
    } else if (this.modelProperty === ExcludeListRadioGroupBase.CONTENT_TYPE_EXCLUDE) {
      items = setting.contentTypeExcludes;
    }
    if (items) {
      items.forEach((item: string): void => {
        const checkBox = new Checkbox(Config(Checkbox, {
          boxLabel: item,
          name: item,
          itemId: item.concat("_checkbox" + this.modelProperty),
          listeners: { change: { fn: bind(this, this.#handleChange) } },
        }));
        this.add(checkBox);
      });
    }
  }

  #handleSettingChange(setting: PropertyChangeEvent): void {
    this.removeAll(true);
    this.#handleSetting(setting.newValue);
  }

  #handleChange(changed: any): void {
    const excludes: Array<any> = this.modelBean.get(this.modelProperty);
    const checkBoxName: string = changed.name;
    if (changed.checked) {
      this.modelBean.set(this.modelProperty, [checkBoxName].concat(excludes));
    } else {
      const clearedList: any = excludes.filter((it: any): boolean =>
        it !== checkBoxName,
      );
      this.modelBean.set(this.modelProperty, clearedList);
    }
  }

}

export default ExcludeListRadioGroupBase;
