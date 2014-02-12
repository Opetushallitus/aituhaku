#!/bin/bash

# Sammuttaa ajossa olevan Aituhaku-prosessin. Ajettava käyttäjänä,
# jolla on riittävät oikeudet Aituhaku-prosessin tappamiseen.
#
# Käyttö:
#
#     ./stop-aituhaku.sh

set -eu

CURRENT_JARFILE='aituhaku.jar'
PIDFILE='aituhaku.pid'

if [ -a $PIDFILE ]
then
    echo "Stopping Aituhaku..."
    kill `cat $PIDFILE` && rm $PIDFILE
fi
