package com.coremedia.blueprint.contentsync.studio.model {
import com.coremedia.ui.data.impl.RemoteBeanImpl;

[RestResource(uriTemplate="contentsync/{path:.+}")]
public class ContentSyncModel extends RemoteBeanImpl implements IContentSyncModel{

  private static const NAME:String = "name";
  private static const CHILDREN:String = "children";
  private static const NUMERIC_ID:String = "numericId";
  private static const IS_LEAF:String = "leaf";
  private static const TYPE:String = "type";
  private static const ID:String = "id";

  public function ContentSyncModel(uri:String){
    super(uri);
  }

  public function getName():String {
    return get(NAME);
  }

  public function getContentId():String{
    return get(ID).toString();
  }

  public function isLeaf():Boolean {
    return get(IS_LEAF);
  }

  public function getNumericId():Number{
    return parseInt(get(NUMERIC_ID));
  }

  public function children():Array {
    return get(CHILDREN);
  }


  public function getType():String {
    return get(TYPE);
  }
}
}
