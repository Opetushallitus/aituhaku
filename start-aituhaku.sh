#!/bin/bash

# Käynnistää Aituhaku-version, johon aituhaku.jar-linkki osoittaa.
# Ajettava Aituhaku-asennushakemistossa käyttäjänä, jolla Aituhaku-prosessia halutaan 
# ajaa.
#
# Käyttö:
#
#     ./start-aituhaku.sh

set -eu

CURRENT_JARFILE='aituhaku.jar'
PIDFILE='aituhaku.pid'

echo "Starting Aituhaku... "
if [ -a $CURRENT_JARFILE ]
then
    nohup java -jar $CURRENT_JARFILE 1> aituhaku.out 2> aituhaku.err &
    echo -n $! > $PIDFILE
else
    echo "Tiedosto '$CURRENT_JARFILE' puuttuu"
    exit 1
fi
