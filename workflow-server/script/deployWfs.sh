#!/bin/bash

CURRENT_DIR=`pwd`
WFS_CMD_PATH=/../../../../../apps/workflow-server/modules/cmd-tools/wfs-tools-application/target/wfs-tools/bin/cm

WFS_DEF=${CURRENT_DIR}/../src/main/resources/com/coremedia/blueprint/contentsync/workflows/studio-partial-sync-workflow.xml
WFS_JAR=${CURRENT_DIR}/../target/workflow-server.labs-beta-ingest-client-1-SNAPSHOT.jar

USER=admin
PASS=admin



${CURRENT_DIR}${WFS_CMD_PATH} upload -u$USER -p$PASS -f${WFS_DEF} -j${WFS_JAR}
