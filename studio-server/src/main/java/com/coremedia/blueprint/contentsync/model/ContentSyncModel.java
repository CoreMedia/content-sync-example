package com.coremedia.blueprint.contentsync.model;

import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel;
import com.coremedia.blueprint.contentsync.client.predicates.ContentPropertyNamePredicate;
import com.coremedia.blueprint.contentsync.client.predicates.ContentTypePredicate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * it is necessary to map all data from the iapi-client to a generic
 * class reflecting all properties / information.
 */
public class ContentSyncModel {

  int id;
  String type;
  String name;
  boolean isLeaf;
  List<ContentSyncModel> children;

  public ContentSyncModel(ContentRefDataModel refModel){
    id = refModel.getNumericIdAsInt();
    isLeaf = refModel.isFolder();
    name = parsePathToName(refModel.getPath());
    type = refModel.getType();
  }

  public ContentSyncModel(ContentDataModel dataModel,
                          ContentTypePredicate contentTypePredicate){
    id = dataModel.getNumericIdAsInt();
    isLeaf = dataModel.isFolder();
    name = parsePathToName(dataModel.getPath());
    type = dataModel.getType();
    children = dataModel
            .getChildren(contentTypePredicate)
            .stream()
            .map(ContentSyncModel::new)
            .collect(Collectors.toList());
  }

  private String parsePathToName(String path){
    return path.substring(path.lastIndexOf("/")+1);
  }

  public String getType() {
    return type.equals("Folder_") ? "Folder" : type;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean isLeaf() {
    return isLeaf;
  }

  public List<ContentSyncModel> getChildren() {
    return children != null ? children : Collections.emptyList();
  }

}
