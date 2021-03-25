package com.coremedia.blueprint.contentsync.studio.model {
import com.coremedia.cap.struct.Struct;

/**
 * Representation of the configuration which needs to be places as Setting into:
 * <Site>/Options/Settings/Content sync/ContentSyncSettings
 *
 * The Setting structure may contain multiple environment configurations:
 *
 *    environments <- StructList
 *      - Environment A <- Struct
 *          name <- String
 *          identifier <- String (needs to be the same as in the studio-webapp configuration)
 *          recursionPartialSync <- Integer (defaults to 1)
 */
public class ContentSyncSettings {

  private static const IDENT:String = "ident";
  private static const RECURSION:String = "recursion";
  private static const NAME:String = "name";

  private var _identifier:String;
  private var _recursionPartialSync:Number;
  private var _name:String;
  private var _brandFolderId:String;
  private var _propertyExcludes:Array;
  private var _contentTypeExcludes:Array;

  public function ContentSyncSettings(struct:Struct,propExcludes:Array, contentExcludes:Array, brandFolderId:String){
    _identifier = struct.get(IDENT);
    _recursionPartialSync = struct.get(RECURSION);
    _name = struct.get(NAME);
    _brandFolderId = brandFolderId.replace("coremedia:///cap/content/","");
    _propertyExcludes = propExcludes;
    _contentTypeExcludes = contentExcludes;
  }

  public function get brandFolderId():String {
    return _brandFolderId;
  }

  public function get identifier():String {
    return _identifier;
  }

  public function get recursionPartialSync():Number {
    return _recursionPartialSync || 1;
  }

  public function get name():String {
    return _name;
  }

  public function get propertyExcludes():Array {
    return _propertyExcludes;
  }

  public function set propertyExcludes(value:Array):void {
    _propertyExcludes = value;
  }

  public function get contentTypeExcludes():Array {
    return _contentTypeExcludes;
  }

  public function set contentTypeExcludes(value:Array):void {
    _contentTypeExcludes = value;
  }
}
}
