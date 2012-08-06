#!/bin/sh
# when using xvnc plug-in in jenkins results in error (if owner of .vnc and .xauthority are builders (tomcat, bitnami) otherwise the 
#invalid type or access error is shown): 
#xauth:  timeout in locking authority file /home/bitnami/.Xauthority
#New 'ip-10-252-138-103:26 (root)' desktop is ip-10-252-138-103:26
#Starting applications specified in /home/bitnami/.vnc/xstartup
#Log file is /home/bitnami/.vnc/ip-10-252-138-103:26.log

# when using manually installed vnc4server with the export DISPLAY=$DISPLAY command, junit tests crash at any point
export PATH=/usr/local/apache-maven-3.0.4/bin:$PATH
export DISPLAY=$DISPLAY
THIS=$(readlink -f $0)
BUNDLE_ROOT="`dirname $THIS`"
env
echo $HOME
cd $BUNDLE_ROOT
mvn -e -Dmaven.repo.local=/var/m2 clean install
