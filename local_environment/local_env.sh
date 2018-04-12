#!/usr/bin/env bash -x

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

declare -A APPS_CONF=(\
    ["styxdev,start_grep_cmd"]="grep \"Started styx server in\" ${SCRIPT_DIR}/startup.log"\
    ["styxdev,stop_grep_cmd"]="grep -e \"styxdev.*ERROR\" ${SCRIPT_DIR}/startup.log | grep -v \"locsClientLoader\""\
    ["checkito,start_grep_cmd"]="grep \"checkito.*Checkito listening for HTTP requests\" ${SCRIPT_DIR}/startup.log"\
    ["checkito,stop_grep_cmd"]="grep -e \"checkito.*ERROR\" ${SCRIPT_DIR}/startup.log"\
    ["ba,start_grep_cmd"]="grep \"ba.*Server startup\" ${SCRIPT_DIR}/startup.log"\
    ["ba,stop_grep_cmd"]="grep -e \"ba.*ERROR\" ${SCRIPT_DIR}/startup.log | grep -v \"locsClientLoader\""\
    ["ba_no_stub,start_grep_cmd"]="grep \"ba.*Server startup\" ${SCRIPT_DIR}/startup.log"\
    ["ba_no_stub,stop_grep_cmd"]="grep -e \"ba.*ERROR\" ${SCRIPT_DIR}/startup.log | grep -v \"locsClientLoader\""\
)

#########################
# APPS DEFAULT VERSIONS #
#########################

BA_VERSION=

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

  		sleep 2
	done
}

function update_env_apps_images {
    docker pull registry.docker.hcom/hotels/checkito:latest > /dev/null 2>&1

    if [ "$?" -eq "1" ]; then
      echo -e "\n$COLOR_HEADER Login to Docker $COLOR_RESET"
      docker login registry.docker.hcom
    fi

    docker pull registry.docker.hcom/hotels/styxpres:release >> ${SCRIPT_DIR}/startup.log 2>&1
    docker pull registry.docker.hcom/hotels/checkito:latest >> ${SCRIPT_DIR}/startup.log 2>&1
    #    docker pull registry.docker.hcom/hotels/cws:latest >> ${SCRIPT_DIR}/startup.log 2>&1
}

###############################
# START/STOP/STATUS FUNCTIONS #
###############################

function get_ba_type {
    BA_TYPE=ba

    while [[ $# > 0 ]]; do
      case $1 in
        -ba-version)
          BA_VERSION=$2
          shift
          ;;
        -no-stub)
          BA_TYPE=ba_no_stub
          shift
          ;;
      esac
      shift
    done

    if [[ ! ${BA_VERSION} ]]; then
        echo "Error! BA version NOT specified (missing -ba-version parameter)!"
        help;
        exit 1
    fi

    export BA_VERSION=${BA_VERSION}

    return ${BA_TYPE}
}

function start-app {
    APP=$1

    if [ "${APP}" == "ba" ]
    then
        APP=$(get_ba_type $@)
    fi

    cd ${SCRIPT_DIR}
    #nohup docker-compose up --no-color ${APP} >> ${SCRIPT_DIR}/startup.log & 2>&1
    echo "starting ${APP}"
    cd ${PREV_DIR}

    watch "Starting ${APP} ..." "${APPS_CONF["${APP},start_grep_cmd"]}" "${APPS_CONF["${APP},stop_grep_cmd"]}"
    RETURN_CODE=$?

    if [ "${RETURN_CODE}" -eq "0" ]
    then
        echo -e "\n$COLOR_SUCCESS ${APP} started $COLOR_RESET"
    else
        echo -e "\n$COLOR_ERROR Error: ${APP} start error $COLOR_RESET"
    fi
}

function stop-app {
    APP=$1

    echo -e "\n$COLOR_HEADER Stopping ${APP} ... $COLOR_RESET"

    cd ${SCRIPT_DIR}
    docker-compose rm -sf ${APP} >> ${SCRIPT_DIR}/startup.log 2>&1
    cd ${PREV_DIR}

    echo "done"
}

function setup {
    echo "" > ${SCRIPT_DIR}/startup.log

    echo -e "\n$COLOR_HEADER Setting up local environment ... $COLOR_RESET"

    update_env_apps_images;

    echo "done"
}

function start {
    setup $@;

    echo -e "\n$COLOR_HEADER Starting local environment ... $COLOR_RESET"

    start-app ba $@
    start-app checkito $@
    start-app styxdev $@
    start-app nginx $@

    echo -e "\n$COLOR_HEADER Local environment started $COLOR_RESET"
}

function stop {
    echo -e "\n$COLOR_HEADER Stopping local environment ... $COLOR_RESET"

    stop-app nginx
    stop-app styxdev
    stop-app checkito
    stop-app ba

    status;

    exit 0;
}

function status {
    echo -e "\n$COLOR_HEADER Status $COLOR_RESET"

	NGNIX_PID=`docker ps -q -f name=nginx`;
	if [ -n "$NGNIX_PID" ]
	then
		echo -e "\n$COLOR_SUCCESS NGINX running. AppId: nginx $COLOR_RESET"
	else
		echo -e "\n$COLOR_ERROR NGINX not running. AppId: nginx $COLOR_RESET"
	fi
	STYX_PID=`docker ps -q -f name=styxdev`;
	if [ -n "$STYX_PID" ]
	then
		echo -e "\n$COLOR_SUCCESS STYX running. AppId: styx $COLOR_RESET"
	else
		echo -e "\n$COLOR_ERROR STYX not running. AppId: styx $COLOR_RESET"
	fi
	CHECKITO_PID=`docker ps -q -f name=checkito`;
	if [ -n "$CHECKITO_PID" ]
	then
		echo -e "\n$COLOR_SUCCESS CHECKITO running. AppId: checkito $COLOR_RESET"
	else
		echo -e "\n$COLOR_ERROR CHECKITO not running. AppId: checkito $COLOR_RESET"
	fi
	BA_PID=`docker ps -q -f name=ba`;
	if [ -n "$BA_PID" ]
	then
		echo -e "\n$COLOR_SUCCESS BookingApp running. AppId: ba $COLOR_RESET"
	else
		echo -e "\n$COLOR_ERROR BookingApp not running. AppId: ba $COLOR_RESET"
	fi
}

########
# HELP #
########

function help {
    echo "Usage: $0 <command> <options>"
    echo "Commands:"
    echo "start -ba-version <ba-version> [-no-stub]     Start the local environment, using the BA version: <ba-version>"
    echo "stop                                          Stop the local environment"
    echo "status                                        Print the local environment status"
    echo "start-app <app_id>                            Start only the specified app (ba|checkito|styx|ngnix)"
    echo "stop-app <app_id>                             Stop only the specified app (ba|checkito|styx|ngnix)"
    echo
    exit 0
}

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
