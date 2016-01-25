#!/bin/bash -i

ST_DIR=`dirname $0`
APPS=
REBUILD=
START=
VERBOSE=

for p in $@;
do
  case ${p} in
    -r) REBUILD=true ;;
    -s) START=true ;;
    -v) VERBOSE=true ;;
    *) APPS="$APPS $p"
  esac
done
[ $VERBOSE ] && echo -e "Apps: $APPS\nRebuild apps: ${REBUILD:-false}\nStart apps: ${START:-false}\n\n"

[ $VERBOSE ] && echo "Validating apps..."
for app in $APPS;
do
  if [ -f "$ST_DIR/rebuild_$app" ] && [ -f "$ST_DIR/start_$app" ]; then
    [ $VERBOSE ] && echo "$app is valid"
  else
    echo "ERROR - $app has no builder or starter script (rebuild_$app or start_$app is missing)"
    exit 1
  fi
done
echo

if [ $REBUILD ]; then
  [ $VERBOSE ] && echo "Rebuilding apps..."
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Rebuilding app: $app"
    eval $ST_DIR/rebuild_$app
  done
fi

if [ $START ]; then
  [ $VERBOSE ] && echo "Starting apps..."
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Starting app: $app"
    eval $ST_DIR/start_$app
  done
fi
