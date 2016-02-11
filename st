#!/bin/bash -i

ST_DIR=`dirname $0`
APPS=
UPDATE=
REBUILD=
START=
VERBOSE=

. $ST_DIR/env.conf

function handleError(){
  echo "ERROR - Something went wrong during $1"
  echo "ERROR - Please check the log file for more details!"
  exit 1
}

function printHelp(){
  echo "Application starter scripts to simplify stubbed environment startup."
  echo "Usage: $0 [-s] [-r] [-u] [-v] app [app..]"
  echo "Parameters:"
  echo "-s            start application"
  echo "-r            rebuild application"
  echo "-u            update application from master branch"
  echo "-u=<branch>   update application from <branch>"
  echo "-v            verbose logging"
  echo "app           list of applications. Available values: apache, stub, ba, hwa, cap, dio, mvt"
  echo
  exit 0
}

for p in $@;
do
  case ${p} in
    -h) printHelp ;;
    -r) REBUILD=true ;;
    -s) START=true ;;
    -u) UPDATE=master ;;
    -u=*) UPDATE=`echo "$p" | sed -e 's/^-u=//'` ;;
    -v) export VERBOSE=true ;;
    *) APPS="$APPS $p"
  esac
done
[ $VERBOSE ] && echo -e "Apps: $APPS\nRebuild apps: ${REBUILD:-false}\nStart apps: ${START:-false}Update branch:$UPDATE\n\n"

if [ $UPDATE ]; then
  echo -e "${COLOR_HEADER} Updating apps ${COLOR_RESET}"
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Updating app: $app"
    $ST_DIR/version_control_update $app $UPDATE || handleError "updating $app" 
  done
fi

if [ $REBUILD ]; then
  echo -e "${COLOR_HEADER} Rebuilding apps ${COLOR_RESET}"
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Rebuilding app: $app"
    $ST_DIR/rebuild_app $app || handleError "rebuilding $app"
  done
fi

if [ $START ]; then
  echo -e "${COLOR_HEADER} Starting apps ${COLOR_RESET}"
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Starting app: $app"
    $ST_DIR/start_app $app || handleError "starting $app"
  done
fi

