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
  appManifests: {
    en: {
      categories: [
        "Content",
      ],
      cmServiceShortcuts: [
        {
          cmKey: "ContentSync",
          cmOrder: 20,
          name: "Content Sync",
          url: "",
          cmCategory: "Content",
          cmService: {
            name: "launchSubAppService",
            method: "launchSubApp",
          },
        },
      ],
    },
  },
  command: {
    build: {
      ignoreTypeErrors: true,
    },
  },
});
