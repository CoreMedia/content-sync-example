import {as, mixin} from "@jangaroo/runtime";
import ContentSyncHelper from "../helper/ContentSyncHelper";
import ContentSyncModel from "./ContentSyncModel";
import IContentSyncReferenceModel from "./IContentSyncReferenceModel";
import RemoteBeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/RemoteBeanImpl";
import Bean from "@coremedia/studio-client.client-core/data/Bean";


class ContentSyncReferenceModel extends RemoteBeanImpl implements IContentSyncReferenceModel {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "contentsync/{ident:[^/]+}/references/{id:[^/]+}/{recursion:[^/]+}";
  static readonly #REFERENCES: string = "references";

  constructor(uri: string) {
    super(uri);
  }

  getReferences(ident: string, modelBean: Bean): Array<any> {
    return as(this.get(ContentSyncReferenceModel.#REFERENCES), Array).map((ref): ContentSyncModel => {
      var csm = as(ContentSyncHelper.getContentById(ref, ident, modelBean), ContentSyncModel);
      return csm;
    });
  }
}

mixin(ContentSyncReferenceModel, IContentSyncReferenceModel);

export default ContentSyncReferenceModel;
