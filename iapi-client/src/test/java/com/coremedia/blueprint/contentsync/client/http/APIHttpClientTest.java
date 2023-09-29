package com.coremedia.blueprint.contentsync.client.http;

import com.coremedia.blueprint.contentsync.client.IAPIContext;
import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import com.coremedia.blueprint.contentsync.client.model.content.ContentDataModel;
import com.coremedia.blueprint.contentsync.client.model.content.ContentRefDataModel;
import com.coremedia.blueprint.contentsync.client.model.property.*;
import com.coremedia.blueprint.contentsync.client.services.IAPIRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class APIHttpClientTest {
  ContentSyncConnectionContext context;
  Map<String, Integer> propertyDict;
  IAPIRepository repository;

  void initRepository() {
    if (repository != null)
      return;
    try {
      URL url = new URL(context.getHost());
      InetAddress.getAllByName(url.getHost());
      HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
      urlConn.connect();

      assertEquals(HttpURLConnection.HTTP_FORBIDDEN, urlConn.getResponseCode());
    } catch (MalformedURLException e) {
      throw new RuntimeException("ContentSync Context does not contain valid hostname: " + context.getHost());
    } catch (UnknownHostException e) {
      throw new RuntimeException("ContentSync Context could not resolve hostname: " + context.getHost());
    } catch (IOException e) {
      throw new RuntimeException("ContentSync Context could not connect to: " + context.getHost());
    }
    repository = IAPIContext.withContext(context).build().getRepository();
  }

  @BeforeEach
  void init() {

    propertyDict = new HashMap() {{
      put(DatePropertyModel.TYPE_NAME, 0);
      put(IntegerPropertyModel.TYPE_NAME, 0);
      put(MarkupPropertyModel.TYPE_NAME, 0);
      put(StringPropertyModel.TYPE_NAME, 0);
      put(StructPropertyModel.TYPE_NAME, 0);
      put(LinklistPropertyModel.TYPE_NAME, 0);
      //put(TempBlobPropertyModel.TYPE_NAME, 0);
      put(UrlBlobPropertyModel.TYPE_NAME, 0);
      put(ServerBlobPropertyModel.TYPE_NAME, 0);
    }};
    context = new ContentSyncConnectionContext(
            "https://ingest.first.sandbox.ps.coremedia.cloud/coremedia/api/ingest/v2/",
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3d3dy5jb3JlbWVkaWEuY29tIiwic3ViIjoiaW1wb3J0ZXIiLCJqdGkiOiJiOWMyYzU1ZC1iNDMxLTRjZmItYmRmZS1kMDE2YmEwM2Y0OTYiLCJpYXQiOjE2NjUzOTUyMDAsInBlcm1pc3Npb25zIjpbImZpcnN0OmNvbnRlbnRfcmVhZCIsImZpcnN0OmNvbnRlbnRfd3JpdGUiXX0.mb2KvJo8cfoS5KXz7iNcidzU1qGZjGYE2feSjcs6q6W0-TcY05--N-RGqXi7dPhlhykeHD61zTBbTvgJLW5wBk5-YqAq-Wchx19pai1mGtCThN3V0qhtPRC1oiQJhpewPswORXtgp9AJuYKk1ziY0T04Nj5Iwju0-tDnXzNj5uQl-TKbwN1rdh5HvsGpvDGlZek8dRYLGqF0Y40M5FGnyrNekACQLpu14iTSyaIGbH2vf92HouLaDf18o38HepUMUKpQD3tJb3EKv-6U6TSjoT8gbwCJaudVjIoWySy-ePwTRq7J4EvjnxHaYdw-OIW7iyiHA8nEbxI9A8CUvCKIIA",
            "administratoren",
            "uat");

    initRepository();
  }

  @Test
  public void testClientAuthentication() {
    IAPIHttpClient client = new IAPIHttpClientImpl();
    client.init(context);
  }

  @Test
  public void testClientIndirectInit() {
    IAPIContext.withContext(context).build().getRepository().getRoot();
  }
  @Test
  public void testContentGetters() {
    String numericArticleID = getAnyArticleID();
    assertNotNull("Failed to find any article", numericArticleID);
    ContentDataModel cm = repository.getContentById(numericArticleID);
    String path = "/Sites/Chef Corp./Germany/German/Editorial/Homepage/Company/Sustainability Article";//cm.getPath();
    repository.getContentByPath(path); // FAIL
  }

  @Test
  public void testGettingProperties() {
    String numericArticleID = getAnyArticleID();
    assertNotNull("Failed to find any article", numericArticleID);
    ContentDataModel content = repository.getContentById(numericArticleID);
    for (String propertyName : content.getProperties().keySet()) {
      PropertyModel property = content.getProperties().get(propertyName);
      System.out.println(propertyName);
      switch (property.getType()) {
        case LinklistPropertyModel.TYPE_NAME:
          LinklistPropertyModel linklist = (LinklistPropertyModel) property;
          break;
        case StructPropertyModel.TYPE_NAME:
          StructPropertyModel struct = (StructPropertyModel) property;
          break;
        case MarkupPropertyModel.TYPE_NAME:
          MarkupPropertyModel markup = (MarkupPropertyModel) property;
          break;
        case StringPropertyModel.TYPE_NAME:
          StringPropertyModel string = (StringPropertyModel) property;
          break;
        case DatePropertyModel.TYPE_NAME:
          DatePropertyModel date = (DatePropertyModel) property;
          break;
        case IntegerPropertyModel.TYPE_NAME:
          IntegerPropertyModel count = (IntegerPropertyModel) property;
          break;
        default:
          System.out.println("need to be added " + property.getType());
      }
    }
  }
  @Test
  public void testGettingPropertiesRoot() {
    ContentRefDataModel sites = getSitesFolder();
    ContentDataModel root = repository.getContentById(sites.getNumericId());
    recursiveSearchProperties(root, propertyDict);

    for (String prop : propertyDict.keySet()) {
      if (propertyDict.get(prop) == 0)
        System.out.println(prop + " was not found");
      else
        System.out.println(prop + " was found " + propertyDict.get(prop) + "times");
    }
  }
  String getAnyContentByType(@NotNull ContentRefDataModel cmRef, String targetType) {
    String type = cmRef.getType();
    if (targetType.equals(type)) {
      return cmRef.getNumericId();
    }
    if ("Folder_".equals(type)) {
      ContentDataModel content = repository.getContentById(cmRef.getNumericId());
      for (ContentRefDataModel child : content.getChildren()) {
        String numericID = getAnyContentByType(child, targetType);
        if (numericID != null)
          return numericID;
      }
    }
    return null;
  }

  void recursiveSearchProperties(@NotNull ContentDataModel cm, @NotNull Map<String, Integer> propertyDict) {
    for (PropertyModel property : cm.getProperties().values()) {
      String name = property.getType();
      Integer count = propertyDict.get(name);
      if (count == null) {
        // System.out.println("need to add " + name);
        propertyDict.put(name, 1);
      } else {
        propertyDict.put(name, count + 1);
      }
    }
    if (0 != Collections.min(propertyDict.values()))
      return;
    for (ContentRefDataModel child : cm.getChildren()) {
      ContentDataModel content = repository.getContentById(child.getNumericId());
      recursiveSearchProperties(content, propertyDict);
    }
  }


  ContentRefDataModel getSitesFolder() {
    for (ContentRefDataModel child : repository.getRoot().getChildren()) {
      if ("/Sites".equals(child.getPath()))
        return child;
    }
    return null;
  }

  String getAnyArticleID() {
    ContentRefDataModel sites = getSitesFolder();
    assertNotNull("Failed to find Sites folder", sites);
    return getAnyContentByType(sites, "CMArticle");
  }


}
