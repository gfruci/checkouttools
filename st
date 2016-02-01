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

for p in $@;
do
  case ${p} in
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
  if [ -f "$ST_DIR/rebuild_$app" ] && [ -f "$ST_DIR/start_$app" ] && [ -f "$ST_DIR/update_$app" ]; then
    [ $VERBOSE ] && echo "$app is valid"
  else
    echo "ERROR - $app has no builder or starter or updater script (rebuild_$app or start_$app or update_$app is missing)"
    exit 1
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

