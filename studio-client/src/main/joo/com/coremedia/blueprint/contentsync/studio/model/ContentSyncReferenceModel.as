package com.coremedia.blueprint.contentsync.studio.model {
import com.coremedia.blueprint.contentsync.studio.helper.ContentSyncHelper;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.impl.RemoteBeanImpl;

[RestResource(uriTemplate="contentsync/{ident:[^/]+}/references/{id:[^/]+}/{recursion:[^/]+}")]
public class ContentSyncReferenceModel extends RemoteBeanImpl implements IContentSyncReferenceModel{
  private static const REFERENCES:String = "references";

  public function ContentSyncReferenceModel(uri:String) {
    super(uri);
  }

  public function getReferences(ident:String, modelBean:Bean):Array{
    return (get(REFERENCES) as Array).map(function (ref):ContentSyncModel{
      var csm:ContentSyncModel = ContentSyncHelper.getContentById(ref,ident,modelBean) as ContentSyncModel;
      return csm;
    })
  }
}
}
