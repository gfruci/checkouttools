#!/usr/bin/env bash -i

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
  echo "-k            terminate application"
  echo "app           list of applications. Available values: apache, stub, ba, hwa, cap, dio, mvt"
  echo
  exit 0
}

function checkForUpdates(){
  echo "Checking for updates..."
  pushd . &> /dev/null
  cd "$ST_DIR"
  git fetch origin master &> /dev/null
  updates=`git status master -sb`
  if [[ "$updates" == *behind* ]]; then
    echo "Updates available. Please get the latest version"
    echo "$updates"
    exit 1
  fi
}

checkForUpdates

for p in $@;
do
  case ${p} in
    -h) printHelp ;;
    -r) REBUILD=true ;;
    -s) START=true ;;
    -u) UPDATE=master ;;
    -k) TERMINATE=true ;;
    -u=*) UPDATE=`echo "$p" | sed -e 's/^-u=//'` ;;
    -v) export VERBOSE=true ;;
    *) APPS="$APPS $p"
  esac
done
[ $VERBOSE ] && echo -e "Apps: $APPS\nRebuild apps: ${REBUILD:-false}\nStart apps: ${START:-false}Update branch:$UPDATE\n\n"

if [ $TERMINATE ]; then
  echo -e "${COLOR_HEADER} Terminating apps ${COLOR_RESET}"
  for app in $APPS;
  do
    [ $VERBOSE ] && echo "Terminating app: $app"
    $ST_DIR/terminate_app $app
  done
fi

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

