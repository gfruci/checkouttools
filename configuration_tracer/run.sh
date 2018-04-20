#!/usr/bin/env bash
PROJECT_ROOT="C:/Users/Csaba_Tolcser/Projects/HCOM/bookingapp"
CONFIG_SOURCE="C:/Users/Csaba_Tolcser/Projects/HCOM/bookingapp/bookingapp-web/src/main/resources/conf/environment"
CREDENTIAL=$1

java -DconfigSource=$CONFIG_SOURCE -DprojectRoot=$PROJECT_ROOT -Dcredential=$CREDENTIAL -jar target/configuration-tracer-1.0-SNAPSHOT.jar "$@"
