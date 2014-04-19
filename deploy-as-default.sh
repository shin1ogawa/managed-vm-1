#!/bin/sh

cp src/main/webapp/WEB-INF/appengine-web.default.xml src/main/webapp/WEB-INF/appengine-web.xml
/usr/local/opt/appengine-java-sdk-1.9.3/bin/appcfg.sh --oauth2 -A shin1-vm-runtime -V shin1-default -s preview.appengine.google.com update src/main/webapp
cp src/main/webapp/WEB-INF/appengine-web.default.xml src/main/webapp/WEB-INF/appengine-web.xml
