package com.coremedia.blueprint.contentsync.studio.model {
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.RemoteBean;

public interface IContentSyncReferenceModel extends RemoteBean{

  function getReferences(ident:String, modelBean:Bean):Array;

}
}
