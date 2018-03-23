#!/usr/bin/env bash
JSP_PATH_PREFIX="C:/Work/bookingapp/bookingapp-web/src/main/webapp"
WEB_SRC="C:/Work/bookingapp/bookingapp-web/src"

java -DjspPathPrefix=$JSP_PATH_PREFIX -DwebSrc=$WEB_SRC -jar target/jsp-tracer-1.0-SNAPSHOT.jar "$@"
