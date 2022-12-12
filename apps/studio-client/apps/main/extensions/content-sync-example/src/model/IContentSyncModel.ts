import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";


abstract class IContentSyncModel extends RemoteBean {

  /**
   * Id of the given remote content delivered by the ingest-service
   * @return the numeric id
   */
  abstract getId(): string;

  /**
   * Name of the remote content delivered by the ingest-service
   * @return the name extracted from the path
   */
  abstract getName(): string;

  /**
   * Information if the given remote content is a folder, used within a tree.
   * @return true in case the remote content is a folder, otherwise false.
   */
  abstract isLeaf(): boolean;

  /**
   * Children information if the remote content is a folder. Please note that
   * children is never null.
   */
  abstract children(): Array<any>;

  /**
   * Type of the current content. Usefull for the calculation of the icon.
   * @return Type of the remote content delivered by the ingest-server
   */
  abstract getType(): string;

  abstract getContentId(): string;
}

export default IContentSyncModel;
