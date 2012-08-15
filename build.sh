#!/bin/sh
export PATH=/usr/local/apache-maven-3.0.4/bin:$PATH
export DISPLAY=$DISPLAY
THIS=$(readlink -f $0)
BUNDLE_ROOT="`dirname $THIS`"
cd $BUNDLE_ROOT
# just build
mvn install -Dmaven.test.skip=true
# install eclemma jar to local maven repo, used to create test coverage
cd testing-project
mvn install:install-file -Dmaven.repo.local=/var/m2 -Dfile=./mvn/eclEmmaEquinox.jar -DgroupId=ch.hsr.ifs.cdt.metriculator -DartifactId=eclemma.runtime.equinox -Dversion=1.1.0.200908261008 -Dpackaging=jar
cd ..
# run tests with code coverage
mvn integration-test verify findbugs:findbugs -Pcoverage -Dmaven.repo.local=/var/m2 -e
