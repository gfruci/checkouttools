#!/bin/bash -i

ST_DIR=`dirname $0`
APPS=
UPDATE=
REBUILD=
START=
VERBOSE=

function handleError(){
  echo "ERROR - Something went wrong during $1"
  echo "ERROR - Please check the log file for more details!"
  exit 1
}

function printHelp(){
  echo "Application starter scripts to simplify stubbed environment startup."
  echo "Usage: $0 [-s] [-r] [-u] [-v] app [app..]"
  echo "Parameters:"
  echo "-s    start application (start_'app')"
  echo "-r    rebuild application (rebuild_'app')"
  echo "-u    update application from master branch (update_'app')"
  echo "-v    verbose logging"
  echo "app   list of applications. Available values: apache, stub, ba, hwa"
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
    -v) export VERBOSE=true ;;
    *) APPS="$APPS $p"
  esac
done
[ $VERBOSE ] && echo -e "Apps: $APPS\nRebuild apps: ${REBUILD:-false}\nStart apps: ${START:-false}\n\n"

[ $VERBOSE ] && echo "Validating apps..."
for app in $APPS;
do
  if [ $REBUILD ] && [ ! -f "$ST_DIR/rebuild_$app" ]; then
    echo "$app doesn't have rebuild script" && exit 1
  fi
  if [ $START ] && [ ! -f "$ST_DIR/start_$app" ]; then
    echo "$app doesn't have start script" && exit 1
  fi
  if [ $UPDATE ] && [ ! -f "$ST_DIR/update_$app" ]; then
    echo "$app doesn't have update script" && exit 1
  fi
done
echo

if [ $UPDATE ]; then
  [ $VERBOSE ] && echo "Updating apps..."
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Updating app: $app"
    $ST_DIR/update_$app $UPDATE || handleError "updating $app" 
  done
fi

if [ $REBUILD ]; then
  [ $VERBOSE ] && echo "Rebuilding apps..."
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Rebuilding app: $app"
    $ST_DIR/rebuild_$app || handleError "rebuilding $app"
  done
fi

if [ $START ]; then
  [ $VERBOSE ] && echo "Starting apps..."
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Starting app: $app"
    $ST_DIR/start_$app || handleError "starting $app"
  done
fi

