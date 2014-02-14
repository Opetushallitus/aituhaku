#!/bin/bash

# Julkaisee annetun Aituhaku-version etäpalvelimelle.
#
# Käyttö:
#
#     ./deploy.sh <aituhaku.jar> <user@host>
#
# Parametrit:
#     <aituhaku.jar>  Polku aituhaku.jar:iin.
#
#     <user@host>     Käyttäjätunnus palvelimella ja palvelimen nimi.
#                     Käyttäjällä tulee olla sudo-oikeudet.

set -eu

if [ $# -lt 2 ]
then
    echo "$0 [-t] <aituhaku.jar> <user@host>"
    exit 1
fi

service=${AITUHAKU_SERVICE:-aituhaku}

version_jarfile=$1
user_host=$2
aituhaku_home=${AITUHAKU_HOME:-/data00/aituhaku}
ssh_key=${AITU_SSH_KEY:-~/.ssh/id_rsa}

set -x

# Ei tarkisteta isäntäavaimia, koska testiajoihin käytettävien
# virtuaalipalvelinten IP:t vaihtuvat, kun ne tuhotaan ja luodaan uudelleen
echo "kopioidaan uusi versio etäpalvelimelle $user_host"
scp -i $ssh_key -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -p start-aituhaku.sh stop-aituhaku.sh $version_jarfile $user_host:~

echo "päivitetään sovellus"

ssh -t -t -i $ssh_key -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $user_host "sudo cp start-aituhaku.sh stop-aituhaku.sh `basename $version_jarfile` $aituhaku_home && cd $aituhaku_home && sudo chown tomcat:tomcat -R $aituhaku_home && sudo /sbin/service $service stop && sudo ln -sf `basename $version_jarfile` aituhaku.jar; sudo /sbin/service $service start && sleep 2"
