#!/bin/bash
set -eu

REPO_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

set -x

cd $REPO_PATH/frontend
npm install
grunt bower
grunt sass-compile

cd $REPO_PATH
lein do clean, uberjar

