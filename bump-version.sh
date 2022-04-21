#!/bin/bash

if [ $# != 2 ]; then
echo "Usage: $0 <version> <version suffix: RELEASE|SNAPSHOT>"
echo "Example: $0 1.7.20 RELEASE"
exit 1
fi

project_dir=$(cd `dirname $0`; pwd)
echo "project_dir: $project_dir"
cd $project_dir
mvn versions:set -DnewVersion=$1-$2 -DgenerateBackupPoms=false