import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import ContentSyncSettings from "../model/ContentSyncSettings";
import ContentSyncLeftContainer from "./ContentSyncLeftContainer";

interface ContentSyncLeftContainerBaseConfig extends Config<Container> {
}

class ContentSyncLeftContainerBase extends Container {
  declare Config: ContentSyncLeftContainerBaseConfig;

  #excludePropertiesHiddenVE: ValueExpression = null;

  #excludeContentTypeHiddenVE: ValueExpression = null;

  constructor(config: Config<ContentSyncLeftContainer> = null) {
    super(config);
    ContentSyncHelper.getContentSyncSettings().then((item: Array<any>): void => {
      if (item.length > 0) {
        const setting: ContentSyncSettings = item[0];
        this.getExcludePropertiesVE().setValue(setting.propertyExcludes);
        this.getExcludeContentTypeVE().setValue(setting.contentTypeExcludes);
      }
    });
  }

  getExcludePropertiesVE(): ValueExpression {
    if (!this.#excludePropertiesHiddenVE) {
      this.#excludePropertiesHiddenVE = ValueExpressionFactory.createFromValue([]);
    }
    return this.#excludePropertiesHiddenVE;
  }

  getExcludeContentTypeVE(): ValueExpression {
    if (!this.#excludeContentTypeHiddenVE) {
      this.#excludeContentTypeHiddenVE = ValueExpressionFactory.createFromValue([]);
    }
    return this.#excludeContentTypeHiddenVE;
  }

  protected hasEntries(data: Array<any>): boolean {
    return data.length > 0;
  }

}

export default ContentSyncLeftContainerBase;
