#!/usr/bin/env bash

COLOR_HEADER="\033[7;49;36m"
COLOR_WATCH="\033[7;49;33m"
COLOR_SUCCESS="\033[7;49;32m"
COLOR_ERROR="\033[7;49;31m"
COLOR_RESET="\033[0m"
COLOR_STEP="\033[7;49;37m"

PREV_DIR=$(pwd)
LINK=$(readlink $0)
if [ "$LINK" = "" ]
then
    LINK=$0
fi
SCRIPT_DIR=$(dirname ${LINK})

cd ${SCRIPT_DIR}
mkdir -p logs
cd ${PREV_DIR}

#########################
# CONFIGS #
#########################

START_MODE=
BA_VERSION=
BMA_VERSION=
STUB_STATUS=

APPS=( "mvt" "ba" "bma" "checkito" "styxpres" "nginx")

declare -A APPS_CONF=(\
    ["mvt,update_cmd"]="docker pull 181651482125.dkr.ecr.us-west-2.amazonaws.com/hotels/mvt:latest >> ${SCRIPT_DIR}/logs/startup.log 2>&1"\
    ["styxpres,start_status_cmd"]="grep \"Started styx server in\" ${SCRIPT_DIR}/logs/styxpres.log"\
    ["styxpres,stop_status_cmd"]="grep -e \"styxpres.*ERROR\" ${SCRIPT_DIR}/logs/styxpres.log | grep -v \"locsClientLoader\""\
    ["styxpres,update_cmd"]="docker pull 181651482125.dkr.ecr.us-west-2.amazonaws.com/hotels/styxpres:release >> ${SCRIPT_DIR}/logs/startup.log 2>&1"\
    ["checkito,start_status_cmd"]="grep \"checkito.*Checkito listening for HTTP requests\" ${SCRIPT_DIR}/logs/checkito.log"\
    ["checkito,stop_status_cmd"]="grep -e \"checkito.*ERROR\" ${SCRIPT_DIR}/logs/checkito.log"\
    ["checkito,update_cmd"]="docker pull 181651482125.dkr.ecr.us-west-2.amazonaws.com/hotels/checkito:latest >> ${SCRIPT_DIR}/logs/startup.log 2>&1"\
    ["ba,start_status_cmd"]="grep \"ba.*Server startup\" ${SCRIPT_DIR}/logs/ba.log"\
    ["ba,stop_status_cmd"]="grep -e \"ba.*ERROR\" ${SCRIPT_DIR}/logs/ba.log | grep -v \"locsClientLoader\""\
    ["bma,start_status_cmd"]="grep \"bma.*Server startup\" ${SCRIPT_DIR}/logs/bma.log"\
    ["bma,stop_status_cmd"]="grep -e \"bma.*ERROR\" ${SCRIPT_DIR}/logs/bma.log | grep -v \"locsClientLoader\""\
)

#####################
# UTILITY FUNCTIONS #
#####################

function watch {
    echo -e "\n$COLOR_WATCH $1 $COLOR_RESET"

    timeStart=`date +%s`

	while :;
  	do
  		eval $2 > /dev/null
  		if [ "$?" -eq "0" ]
  		then
  			return 0
  		else
  			ERROR=$(eval $3)
  			if [ "$?" -eq "0" ]
  			then
  			    echo $ERROR
  				return 1
  			fi
  		fi

        timeEnd=`date +%s`
        timeTotal=$((timeEnd-timeStart))
        if [ ${timeTotal} -gt 600 ]
  		then
            echo "Error! The application took too much time to start."
  			return 1
        fi

  		sleep 5
	done
}

function update_env_apps_images {
    docker pull 181651482125.dkr.ecr.us-west-2.amazonaws.com/hotels/mvt:latest > /dev/null 2>&1

    if [ "$?" -eq "1" ]; then
      command -v docker_login > /dev/null 2>&1
      if [ "$?" -eq "0" ]; then
        echo -e "\n$COLOR_HEADER Login to Docker $COLOR_RESET"
        docker_login
      else
        echo -e "\nOps, docker_login command not found, please login to docker manually!"
        echo -e "For more info on docker_login command check https://confluence/display/HCOMCheckout/Payments+-+AWS+ECR+Docker+Login"
        exit 1
      fi
    fi


    for APP in "${APPS[@]}"
    do
        UDPATE_CMD=${APPS_CONF["${APP},update_cmd"]}
        if [ -n "${UDPATE_CMD}" ]
        then
            echo "Updating ${APP} ..."
            $(eval ${UDPATE_CMD})
        fi
    done
}

###############################
# START/STOP/STATUS FUNCTIONS #
###############################

function setup_app_versions {
    while [[ $# > 0 ]]; do
      case $1 in
        -ba-version)
          BA_VERSION=$2
          export BA_VERSION=${BA_VERSION}
          shift
          ;;
        -no-stub)
          STUB_STATUS=_no_stub
          shift
          ;;
        -bma-version)
          BMA_VERSION=$2
          export BMA_VERSION=${BMA_VERSION}
          shift
          ;;
      esac
      shift
    done


}

function start-app {
    APP=$1
    APP_TYPE=""

    echo "" > ${SCRIPT_DIR}/logs/${APP}.log

    if [ "${APP}" == "ba" ]
    then
        APP_TYPE=${STUB_STATUS}
        if [ "${BA_VERSION}" == "" ]
        then
            if [ "${START_MODE}" == "start-all" ]
            then
                return 1;
            else
                echo "Error! BA version NOT specified (missing -ba-version parameter)!"
                help;
                exit 1
            fi
        fi
    fi

    if [ "${APP}" == "bma" ]
    then
        APP_TYPE=${STUB_STATUS}
        if [ "${BMA_VERSION}" == "" ]
        then
            if [ "${START_MODE}" == "start-all" ]
            then
                return 1;
            else
                echo "Error! BMA version NOT specified (missing -bma-version parameter)!"
                help;
                exit 1
            fi
        fi
    fi

    cd ${SCRIPT_DIR}
    nohup docker-compose up --no-color ${APP}${APP_TYPE} >> ${SCRIPT_DIR}/logs/${APP}.log & 2>&1
    cd ${PREV_DIR}

    watch "Starting ${APP} ..." "${APPS_CONF["${APP},start_status_cmd"]}" "${APPS_CONF["${APP},stop_status_cmd"]}"
    WATCH_RETURN_CODE=$?

    if [ "${WATCH_RETURN_CODE}" -eq "0" ]
    then
        echo -e "\n$COLOR_SUCCESS ${APP} started $COLOR_RESET"
    else
        echo -e "\n$COLOR_ERROR Error: ${APP} start error $COLOR_RESET"
        if [ "${START_MODE}" == "start-all" ]
        then
            stop
        else
            stop-app $APP
        fi
        exit 1
    fi
}

function stop-app {
    APP=$1

    echo -e "\n$COLOR_HEADER Stopping ${APP} ... $COLOR_RESET"

    cd ${SCRIPT_DIR}
    docker rm -f ${APP} >> ${SCRIPT_DIR}/logs/${APP}.log 2>&1
    cd ${PREV_DIR}

    echo "done"
}

function setup {
    echo "" > ${SCRIPT_DIR}/logs/startup.log

    echo -e "\n$COLOR_HEADER Setting up local environment ... $COLOR_RESET"

    git fetch >> ${SCRIPT_DIR}/logs/startup.log 2>&1
    git status | grep "origin/master"

	NO_UPDATE=0
	for var in "$@"
	do
	  if [ ${var} = "-skip-update" ]
	  then
	    NO_UPDATE=1
	  fi
	done
	if [ ${NO_UPDATE} -lt 1 ]
	then
	  update_env_apps_images;
	fi

    echo "done"
}

function start {
    START_MODE="start-all"

    setup $@;

    echo -e "\n$COLOR_HEADER Starting local environment ... $COLOR_RESET"

    for APP in "${APPS[@]}"
    do
        start-app ${APP} $@
    done

    echo -e "\n$COLOR_HEADER Local environment started $COLOR_RESET"
}

function stop {
    echo -e "\n$COLOR_HEADER Stopping local environment ... $COLOR_RESET"

    for APP in "${APPS[@]}"
    do
        stop-app ${APP}
    done

    status;

    exit 0;
}

function status {
    echo -e "\n$COLOR_HEADER Status $COLOR_RESET"

    for APP in "${APPS[@]}"
    do
    	PID=`docker ps -q -f name=${APP}`;
        if [ -n "$PID" ]
        then
            echo -e "\n$COLOR_SUCCESS ${APP} running. $(eval ${APPS_CONF["${APP},status_cmd"]}) $COLOR_RESET"
        else
            echo -e "\n$COLOR_ERROR ${APP} not running. $COLOR_RESET"
        fi
    done
}

########
# HELP #
########

function help {
    echo "Usage: $0 <command> <options>"
    echo "Commands:"
    echo "start [-skip-update]                                         Start the local environment, with no front-end apps (BA)"
    echo "start -ba-version <ba-version> [-no-stub] [-skip-update]     Start the local environment, using the BA version: <ba-version>"
    echo "start -bma-version <bma-version> [-no-stub] [-skip-update]   Start the local environment, using the BMA version: <bma-version>"
    echo "stop                                                         Stop the local environment"
    echo "status                                                       Print the local environment status"
    echo "start-app <app_id>                                           Start only the specified app ($(for APP in "${APPS[@]}"; do echo -n " ${APP}"; done) )"
    echo "stop-app <app_id>                                            Stop only the specified app ($(for APP in "${APPS[@]}"; do echo -n " ${APP}"; done) )"
    echo
    exit 0
}

setup_app_versions $@

case "$1" in
	start)
	    shift
		start $@;;
	stop)
	    shift
	    stop $@;;
	status)
	    shift
		status $@;;
	start-app)
	    shift
		start-app $@;;
	stop-app)
	    shift
	    stop-app $@;;
	*)
	    help;;
esac
