import Bean from "@coremedia/studio-client.client-core/data/Bean";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";

abstract class IContentSyncReferenceModel extends RemoteBean {

  abstract getReferences(ident: string, modelBean: Bean): Array<any>;

}

export default IContentSyncReferenceModel;
