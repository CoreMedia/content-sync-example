import Bean from "@coremedia/studio-client.client-core/data/Bean";
import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import BoundRadioGroup from "@coremedia/studio-client.ext.ui-components/components/BoundRadioGroup";
import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import CollapsiblePanel from "@coremedia/studio-client.main.editor-components/sdk/premular/CollapsiblePanel";
import Button from "@jangaroo/ext-ts/button/Button";
import Radio from "@jangaroo/ext-ts/form/field/Radio";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import { asConfig, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ContentSyncPluginResources_properties from "../ContentSyncPluginResources_properties";
import TriggerWorkflowAction from "../action/TriggerWorkflowAction";
import ExcludeListRadioGroup from "../component/ExcludeListRadioGroup";
import ExcludeListRadioGroupBase from "../component/ExcludeListRadioGroupBase";
import SettingsAwareRadioGroup from "../component/SettingsAwareRadioGroup";
import ContentSyncEditorBase from "../editor/ContentSyncEditorBase";
import RunningWfsInstancesGridPanel from "../wfs/RunningWfsInstancesGridPanel";
import ContentSyncLeftContainerBase from "./ContentSyncLeftContainerBase";

interface ContentSyncLeftContainerConfig extends Config<ContentSyncLeftContainerBase>, Partial<Pick<ContentSyncLeftContainer,
  "modelBean"
>> {
}

class ContentSyncLeftContainer extends ContentSyncLeftContainerBase {
  declare Config: ContentSyncLeftContainerConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.contentsync.studio.container.contentSyncLeftContainer";

  #modelBean: Bean = null;

  get modelBean(): Bean {
    return this.#modelBean;
  }

  set modelBean(value: Bean) {
    this.#modelBean = value;
  }

  #convertId(id: string): string {
    if (!id) {
      return "unknown_panel";
    }
    return id.concat("_panel");
  }

  constructor(config: Config<ContentSyncLeftContainer> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(ContentSyncLeftContainer, {
      flex: 2,
      ui: ConfigUtils.asString(ContainerSkin.DARK_200),
      height: "100%",
      items: [
        Config(CollapsiblePanel, {
          title: ContentSyncPluginResources_properties.ContentSync_Selection,
          collapsible: false,
          collapsed: false,
          items: [
            Config(BoundRadioGroup, {
              defaultValue: ContentSyncEditorBase.PARTIAL_SYNC_ID,
              toValue: bind(this$, this$.#convertId),
              bindTo: new ConfigBasedValueExpression({
                context: config.modelBean,
                expression: "selectedSyncMode",
              }),
              items: [
                Config(Radio, {
                  itemId: ContentSyncEditorBase.PARTIAL_SYNC_ID,
                  boxLabel: ContentSyncPluginResources_properties.ContentSync_PartialSync_Name,
                }),
                /*
             Extend here for more synchronization modes
             */
              ],
            }),
          ],
        }),
        Config(CollapsiblePanel, {
          title: ContentSyncPluginResources_properties.ContentSync_Environments,
          collapsible: false,
          collapsed: false,
          items: [
            Config(SettingsAwareRadioGroup, {
              vertical: false,
              columns: 1,
              modelBean: config.modelBean,
            }),
          ],
        }),
        Config(CollapsiblePanel, {
          title: ContentSyncPluginResources_properties.ContentSync_WFS_Panel_Name,
          collapsible: false,
          collapsed: false,
          items: [
            Config(Button, {
              ui: ConfigUtils.asString(ButtonSkin.MATERIAL_SECONDARY),
              text: ContentSyncPluginResources_properties.ContentSync_WFS_StartButton_Label,
              baseAction: new TriggerWorkflowAction({ modelBean: config.modelBean }),
            }),
          ],
          layout: Config(HBoxLayout),
        }),
        Config(CollapsiblePanel, {
          title: ContentSyncPluginResources_properties.ContentSync_PropertySelect_Name,
          collapsible: false,
          collapsed: false,
          items: [
            Config(ExcludeListRadioGroup, {
              modelBean: config.modelBean,
              modelProperty: ExcludeListRadioGroupBase.PROPERTY_EXCLUDE,
            }),
          ],
          layout: Config(HBoxLayout),
          plugins: [
            Config(BindVisibilityPlugin, {
              bindTo: this$.getExcludePropertiesVE(),
              transformer: bind(this$, this$.hasEntries),
            }),
          ],
        }),
        Config(CollapsiblePanel, {
          title: ContentSyncPluginResources_properties.ContentSync_ContentTypeSelect_Name,
          collapsible: false,
          collapsed: false,
          items: [
            Config(ExcludeListRadioGroup, {
              modelBean: config.modelBean,
              modelProperty: ExcludeListRadioGroupBase.CONTENT_TYPE_EXCLUDE,
            }),
          ],
          layout: Config(HBoxLayout),
          plugins: [
            Config(BindVisibilityPlugin, {
              bindTo: this$.getExcludeContentTypeVE(),
              transformer: bind(this$, this$.hasEntries),
            }),
          ],
        }),

        Config(CollapsiblePanel, {
          title: ContentSyncPluginResources_properties.ContentSync_WFS_Running_Processes,
          collapsible: false,
          collapsed: false,
          items: [
            Config(RunningWfsInstancesGridPanel),
          ],
        }),

      ],
      plugins: [
        Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.HORIZONTAL_SPACING_MODIFIER_200 }),
      ],
    }), config));
  }
}

export default ContentSyncLeftContainer;
