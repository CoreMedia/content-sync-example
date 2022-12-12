package com.coremedia.blueprint.contentsync.client.xml;

import com.coremedia.blueprint.contentsync.client.IAPIContext;
import com.coremedia.blueprint.contentsync.client.context.ContentSyncConnectionContext;
import com.coremedia.blueprint.contentsync.client.model.auth.CloudToken;
import org.junit.Test;

public class CloudTokenTest {

  @Test
  public void testCloudAuthentication() {
    ContentSyncConnectionContext context = new ContentSyncConnectionContext(
            "http://ingest.first.sandbox.ps.coremedia.cloud/",
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3d3dy5jb3JlbWVkaWEuY29tIiwic3ViIjoiaW1wb3J0ZXIiLCJqdGkiOiJiOWMyYzU1ZC1iNDMxLTRjZmItYmRmZS1kMDE2YmEwM2Y0OTYiLCJpYXQiOjE2NjUzOTUyMDAsInBlcm1pc3Npb25zIjpbImZpcnN0OmNvbnRlbnRfcmVhZCIsImZpcnN0OmNvbnRlbnRfd3JpdGUiXX0.mb2KvJo8cfoS5KXz7iNcidzU1qGZjGYE2feSjcs6q6W0-TcY05--N-RGqXi7dPhlhykeHD61zTBbTvgJLW5wBk5-YqAq-Wchx19pai1mGtCThN3V0qhtPRC1oiQJhpewPswORXtgp9AJuYKk1ziY0T04Nj5Iwju0-tDnXzNj5uQl-TKbwN1rdh5HvsGpvDGlZek8dRYLGqF0Y40M5FGnyrNekACQLpu14iTSyaIGbH2vf92HouLaDf18o38HepUMUKpQD3tJb3EKv-6U6TSjoT8gbwCJaudVjIoWySy-ePwTRq7J4EvjnxHaYdw-OIW7iyiHA8nEbxI9A8CUvCKIIA",
            "https://api.ps.coremedia.cloud/v1/",
            "administratoren",
            true,
            "uat");
    CloudToken tokenWithTTL = IAPIContext.withContext(context).build().getRepository().getCloudAccessToken(200);
    String a = "b";
  }
}
