import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";


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
class ContentSyncSettings {

  static readonly #IDENT: string = "ident";
  static readonly #RECURSION: string = "recursion";
  static readonly #NAME: string = "name";

  #_identifier: string = null;
  #_recursionPartialSync: number = NaN;
  #_name: string = null;
  #_brandFolderId: string = null;
  #_propertyExcludes: Array<any> = null;
  #_contentTypeExcludes: Array<any> = null;

  constructor(struct: Struct, propExcludes: Array<any>, contentExcludes: Array<any>, brandFolderId: string) {
    this.#_identifier = struct.get(ContentSyncSettings.#IDENT);
    this.#_recursionPartialSync = struct.get(ContentSyncSettings.#RECURSION);
    this.#_name = struct.get(ContentSyncSettings.#NAME);
    this.#_brandFolderId = brandFolderId.replace("coremedia:///cap/content/", "");
    this.#_propertyExcludes = propExcludes;
    this.#_contentTypeExcludes = contentExcludes;
  }

  get brandFolderId(): string {
    return this.#_brandFolderId;
  }

  get identifier(): string {
    return this.#_identifier;
  }

  get recursionPartialSync(): number {
    return this.#_recursionPartialSync || 1;
  }

  get name(): string {
    return this.#_name;
  }

  get propertyExcludes(): Array<any> {
    return this.#_propertyExcludes;
  }

  set propertyExcludes(value: Array<any>) {
    this.#_propertyExcludes = value;
  }

  get contentTypeExcludes(): Array<any> {
    return this.#_contentTypeExcludes;
  }

  set contentTypeExcludes(value: Array<any>) {
    this.#_contentTypeExcludes = value;
  }
}

export default ContentSyncSettings;
