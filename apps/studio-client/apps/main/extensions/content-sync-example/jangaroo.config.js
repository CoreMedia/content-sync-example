const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__studio-client.labs-beta-ingest-client",
    namespace: "com.coremedia.blueprint.contentsync.studio",
    css: [
      {
        path: "resources/css/contentsync.css",
      },
    ],
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.contentsync.studio.ContentSyncStudioPlugin",
        name: "ContentSyncPlugin",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true,
    },
  },
  appManifests: {
    en: {
      categories: [
        "Content",
      ],
      cmServiceShortcuts: [
        {
          cmKey: "cmContentSync",
          cmOrder: 30,
          cmCategory: "Content",
          name: "Content-Sync",
          url: "",
          cmAdministrative: true,
          cmService: {
            name: "launchSubAppService",
            method: "launchSubApp",
          },
        },
      ],
    },
  },
});
