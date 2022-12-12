import RemoteBeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/RemoteBeanImpl";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import { as, mixin } from "@jangaroo/runtime";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import ContentSyncModel from "./ContentSyncModel";
import IContentSyncReferenceModel from "./IContentSyncReferenceModel";

class ContentSyncReferenceModel extends RemoteBeanImpl implements IContentSyncReferenceModel {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "contentsync/{ident:[^/]+}/references/{id:[^/]+}/{recursion:[^/]+}";

  static readonly #REFERENCES: string = "references";

  constructor(uri: string) {
    super(uri);
  }

  getReferences(ident: string, modelBean: Bean): Array<any> {
    return (this.get(ContentSyncReferenceModel.#REFERENCES) as Array).map((ref): ContentSyncModel =>{
      const csm = as(ContentSyncHelper.getContentById(ref, ident, modelBean), ContentSyncModel);
      return csm;
    });
  }
}
mixin(ContentSyncReferenceModel, IContentSyncReferenceModel);

export default ContentSyncReferenceModel;