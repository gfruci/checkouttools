#!/bin/bash -i

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
[ $VERBOSE ] && echo -e "Apps: $APPS\nRebuild apps: ${REBUILD:-false}\nStart apps: ${START:-false}"

[ $VERBOSE ] && echo "Validating apps..."
for app in $APPS;
do
  if [ -f "rebuild_$app" ] && [ -f "start_$app" ]; then
    [ $VERBOSE ] && echo "$app is valid"
  else
    echo "ERROR - $app has no builder or starter script (rebuild_$app or start_$app is missing)"
    exit 1
  fi
done

if [ $REBUILD ]; then
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Rebuilding app: $app"
    eval rebuild_$app
  done
fi

if [ $START ]; then
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Starting app: $app"
    eval start_$app
  done
fi
