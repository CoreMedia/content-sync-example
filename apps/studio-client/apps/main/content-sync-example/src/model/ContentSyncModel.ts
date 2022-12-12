import {mixin} from "@jangaroo/runtime";
import IContentSyncModel from "./IContentSyncModel";
import RemoteBeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/RemoteBeanImpl";


class ContentSyncModel extends RemoteBeanImpl implements IContentSyncModel {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "contentsync/{path:.+}";

  static readonly #NAME: string = "name";
  static readonly #CHILDREN: string = "children";
  static readonly #NUMERIC_ID: string = "numericId";
  static readonly #IS_LEAF: string = "leaf";
  static readonly #TYPE: string = "type";
  static readonly #ID: string = "id";

  constructor(uri: string) {
    super(uri);
  }

  getName(): string {
    return this.get(ContentSyncModel.#NAME);
  }

  getContentId(): string {
    return this.get(ContentSyncModel.#ID).toString();
  }

  isLeaf(): boolean {
    return this.get(ContentSyncModel.#IS_LEAF);
  }

  getNumericId(): number {
    return parseInt(this.get(ContentSyncModel.#NUMERIC_ID));
  }

  children(): Array<any> {
    return this.get(ContentSyncModel.#CHILDREN);
  }


  getType(): string {
    return this.get(ContentSyncModel.#TYPE);
  }
}

mixin(ContentSyncModel, IContentSyncModel);

export default ContentSyncModel;
