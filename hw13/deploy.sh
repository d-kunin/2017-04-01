#!/usr/bin/env bash

mvn clean package

# deploy to tomcat
cp target/kunin.war $CATALINA_HOME/webapps/kunin.war
