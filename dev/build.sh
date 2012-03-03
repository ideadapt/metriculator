#!/bin/sh

export PATH=/usr/local/apache-maven/apache-maven-3.0.3/bin:$PATH
export DISPLAY=$DISPLAY
THIS=$(readlink -f $0)
BUNDLE_ROOT="`dirname $THIS`"

cd $BUNDLE_ROOT
mvn -e clean install
