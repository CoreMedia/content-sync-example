package com.coremedia.blueprint.contentsync.studio.helper {
import com.coremedia.blueprint.contentsync.studio.component.ExcludeListRadioGroupBase;
import com.coremedia.blueprint.contentsync.studio.constant.ContentSyncConstants;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncModel;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncReferenceModel;
import com.coremedia.blueprint.contentsync.studio.model.ContentSyncSettings;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtil;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.plugins.FolderTreeNode;

import ext.Deferred;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

internal class ContentSyncHelperImpl implements IContentSyncHelper {

  private static const CS_BASE_URL:String = "contentsync/";
  private static const CS_ID_SEGMENT:String = "/content/id/";
  private static const CS_WFS_RUNNING:String = "/running";
  private static const CS_WFS_ABORT:String = "/abort/";
  private static const REFERENCES:String = "/references/";
  private static const CS_SETTING_LOCATION:String = "/Settings/Options/Settings/Content sync/ContentSyncSettings";
  private static const CM_SETTINGS_SETTINGS_PROP:String = "settings";
  private static const ENVIRONMENTS:String = "environments";
  private static const NO_IDENT:String = "NO_IDENT";
  private static const PROPERTY_EXCLUDES:String = "propertyExcludes";
  private static const CONTENT_TYPE_EXCLUDES:String = "contentTypeExcludes";
  private static var resourceManager:IResourceManager = ResourceManager.getInstance();

  public function getContentById(id:String, ident:String, modelBean:Bean):ContentSyncModel {
    var url:String = CS_BASE_URL
            .concat(ident)
            .concat(CS_ID_SEGMENT)
            .concat(id).concat("?");
    return beanFactory.getRemoteBean(addExclusions(url, ExcludeListRadioGroupBase.CONTENT_TYPE_EXCLUDE, modelBean)) as ContentSyncModel;
  }


  private static function addExclusions(url:String, modelProp:String, modelBean:Bean):String {
    var contentTypeExclusions:Array = modelBean.get(modelProp) || [];
    return url.concat(modelProp)
            .concat("=")
            .concat(contentTypeExclusions.join(","));
  }

  public function startWorkflow(modelBean:Bean):void {
    var css:ContentSyncSettings = modelBean.get(ContentSyncConstants.SELECTED_ENVIRONMENT_SETTING);
    var selectedSync:String = modelBean.get(ContentSyncConstants.SELECTED_SYNC_MODE);
    var allContents:Array = modelBean.get(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY);
    var url:String = CS_BASE_URL
            .concat(css.identifier)
            .concat("/startworkflow/")
            .concat(selectedSync);

    var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod(url, 'POST', true, true);
    remoteServiceMethod.request({
      remoteSyncIds: allContents.map(function (item:FolderTreeNode) {
        return item.data.id;
      })
    }, function (ok):void {
      trace("[ContentSyncHelper] started workflow for " + selectedSync + " " + css.identifier);
    });


  }

  public function getContentSyncSettings():Deferred {
    var def:Deferred = new Deferred();

    editorContext.getSession().getConnection().getContentRepository()
            .getChild(CS_SETTING_LOCATION, function (setting:Content):void {
              if (!setting) {
                def.resolve([]);
                return;
              }
              (setting.getProperties()
                      .get(CM_SETTINGS_SETTINGS_PROP) as RemoteBean).load(function (baseStruct:Struct):void {
                var envList:Array = baseStruct.get(ENVIRONMENTS);
                var propertyExcludes:Array = baseStruct.get(PROPERTY_EXCLUDES);
                var contentTypeExcludes:Array = baseStruct.get(CONTENT_TYPE_EXCLUDES);
                if (!envList) {
                  def.resolve([]);
                }
                def.resolve(envList.map(function (item:Struct):ContentSyncSettings {
                  return new ContentSyncSettings(item, propertyExcludes, contentTypeExcludes, "1");
                }));
              });
            });
    return def;
  }

  public function getRunningInstances():Deferred {
    var def:Deferred = new Deferred();
    var remoteBean:RemoteBean = beanFactory.getRemoteBean(CS_BASE_URL
            .concat(NO_IDENT)
            .concat(CS_WFS_RUNNING)
            .concat("?_ds="+new Date().time));
    if (!remoteBean.isLoaded()) {
      remoteBean.load(function (data:RemoteBean):void {
        var dataArray:Array = data.get("items");
        var runningInstances:Array =dataArray;

        def.resolve(runningInstances.map(function (item){
          item.name = resourceManager.getString('com.coremedia.blueprint.contentsync.studio.ContentSyncPluginResources',item.name.concat('_Name'));
          return item;
        }) || []);
      });
    }
    return def;
  }

  public function getReferencesFor(ident:String, id:String, recursion:Number, modelBean:Bean):Deferred {
    var def:Deferred = new Deferred();
    var url:String = CS_BASE_URL
            .concat(ident)
            .concat(REFERENCES)
            .concat(id)
            .concat("/")
            .concat(recursion)
            .concat("?");
    url = addExclusions(url,ExcludeListRadioGroupBase.PROPERTY_EXCLUDE,modelBean).concat("&");
    url = addExclusions(url,ExcludeListRadioGroupBase.CONTENT_TYPE_EXCLUDE,modelBean);
    var remoteBean:RemoteBean = beanFactory.getRemoteBean(url) as ContentSyncReferenceModel;
    remoteBean.load(function (bean:ContentSyncReferenceModel):void {
      def.resolve(bean);
    });
    return def;
  }

  public function contentSyncModel2FolderTreeNode(csm:ContentSyncModel, parent:FolderTreeNode):FolderTreeNode {
    var id:String = csm.getContentId();
    var node:FolderTreeNode = new FolderTreeNode({
      id: id.toString(),
      text: csm.getName(),
      leaf: true,
      iconCls: "content-type-xs " + ContentLocalizationUtil.getIconStyleClassForContentTypeName(csm.getType())
    });
    node.parentNode = parent;
    return node;
  }

  public function abortWorkflow(id:String):void{
    beanFactory.getRemoteBean(
            CS_BASE_URL
                    .concat(NO_IDENT)
                    .concat(CS_WFS_ABORT)
                    .concat(id)
    ).load();
  }

  public function synchronizeContentList(model:Bean, origFn:FolderTreeNode):void {
    var oldValue:Array = model.get(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY);
    var newContentListValue:* = oldValue.filter(function (item):Boolean {
      return item.data.id !== origFn.data.id;
    });
    model.set(ContentSyncConstants.CONTENT_LIST_BEAN_PROPERTY, newContentListValue);
  }
}
}
