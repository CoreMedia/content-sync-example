#!/bin/bash

CURRENT_DIR=`dirname "$0"`
WFS_CMD_PATH=${CURRENT_DIR}/../../../../../apps/workflow-server/modules/cmd-tools/wfs-tools-application/target/wfs-tools/bin/cm

WFS_DEF=../../../../../../../modules/extensions/content-sync-example/workflow-server/src/main/resources/com/coremedia/blueprint/contentsync/workflows/studio-partial-sync-workflow.xml
WFS_JAR=../../../../../../../modules/extensions/content-sync-example/workflow-server/target/workflow-server.labs-beta-ingest-client-1-SNAPSHOT.jar

USER=admin
PASS=admin

${WFS_CMD_PATH} upload -u$USER -p$PASS -f${WFS_DEF} -j${WFS_JAR}
