#!/bin/sh

export PATH=/usr/local/apache-maven-3.0.4/bin:$PATH
#export DISPLAY=$DISPLAY
THIS=$(readlink -f $0)
BUNDLE_ROOT="`dirname $THIS`"
echo $HOME
cd $BUNDLE_ROOT
mvn -e -Dmaven.repo.local=$HOME/.m2/repository clean install
