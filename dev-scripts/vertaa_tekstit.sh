#!/bin/bash
set -eu

REPO_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

echo "tekstit*.properties tiedostojen erot:"
cd $REPO_PATH/resources/i18n
diff <(cat tekstit.properties | grep '^[[:alpha:]]' | cut -d'=' -f1) <(cat tekstit_sv.properties | grep '^[[:alpha:]]' | cut -d'=' -f1)