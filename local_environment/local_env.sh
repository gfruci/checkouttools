#!/usr/bin/env bash -i

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
  			eval $3 > /dev/null
  			if [ "$?" -eq "0" ]
  			then
  				return 1
  			fi
  		fi

        timeEnd=`date +%s`
        timeTotal=$((timeEnd-timeStart))
        if [ ${timeTotal} -gt 300 ]
  		then
  			return 1
        fi

  		sleep 2
	done
}

function update_env_apps_images {
    loginResult=$(echo "" | docker pull registry.docker.hcom/hotels/checkito:latest > /dev/null 2>&1)

    if [[ $loginResult = *"unauthorized"* ]]; then
      echo -e "\n$COLOR_HEADER Login to Docker ... $COLOR_RESET"
      docker login registry.docker.hcom
    fi

    docker pull registry.docker.hcom/hotels/styxpres:release >> ${SCRIPT_DIR}/startup.log 2>&1
    docker pull registry.docker.hcom/hotels/checkito:latest >> ${SCRIPT_DIR}/startup.log 2>&1
}

function setup_apps_versions {
    while [[ $# > 0 ]]; do
      case $1 in
        -ba)
          BA_VERSION=$2
          shift
          ;;
      esac
      shift
    done

    if [[ ! ${BA_VERSION} ]]; then
        echo "Error! BA version specified (missing -ba parameter)!"
        exit 1
    fi

    export BA_VERSION=${BA_VERSION}
}

###############################
# START/STOP/STATUS FUNCTIONS #
###############################

function start {
    echo "Starting local environment" > ${SCRIPT_DIR}/startup.log

    echo -e "\n$COLOR_HEADER Starting local environment ... $COLOR_RESET"

    setup_apps_versions $@;

    update_env_apps_images;

    cd $SCRIPT_DIR
    nohup docker-compose up --no-color >> ${SCRIPT_DIR}/startup.log & 2>&1
    cd $PREV_DIR

    watch "Starting STYX ..." "grep \"Started styx server in\" ${SCRIPT_DIR}/startup.log" "grep \"styx\" ${SCRIPT_DIR}/startup.log | grep -e \"ERROR\""
    START_STYX_RETURN_CODE=$?

    if [ "$START_STYX_RETURN_CODE" -eq "0" ]
    then
        echo -e "\n$COLOR_SUCCESS STYX started $COLOR_RESET"
    else
        echo -e "\n$COLOR_ERROR Error: STYX start error $COLOR_RESET"
        stop;
    fi

    watch "Starting CHECKITO ..." "grep \"Checkito listening for HTTP requests\" ${SCRIPT_DIR}/startup.log" "grep \"checkito\" ${SCRIPT_DIR}/startup.log | grep -e \"ERROR\""
    START_CHECKITO_RETURN_CODE=$?

    if [ "$START_CHECKITO_RETURN_CODE" -eq "0" ]
    then
        echo -e "\n$COLOR_SUCCESS CHECKITO started $COLOR_RESET"
    else
        echo -e "\n$COLOR_ERROR Error: CHECKITO start error $COLOR_RESET"
        stop;
    fi
}

function stop {
    NGNIX_PID=`docker ps -q -f name=nginx`;
    if [ -n "$NGNIX_PID" ]
	then
		docker kill "$NGNIX_PID" > /dev/null
	fi
	STYX_PID=`docker ps -q -f name=styx`;
    if [ -n "$STYX_PID" ]
	then
		docker kill "$STYX_PID" > /dev/null
	fi
	CHECKITO_PID=`docker ps -q -f name=checkito`;
    if [ -n "$CHECKITO_PID" ]
	then
		docker kill "$CHECKITO_PID" > /dev/null
	fi
	BA_PID=`docker ps -q -f name=ba`;
    if [ -n "$BA_PID" ]
	then
		docker kill "$BA_PID" > /dev/null
	fi

    status;

    exit;
}

function status {
    echo -e "\n$COLOR_HEADER Status $COLOR_RESET"

	NGNIX_PID=`docker ps -q -f name=nginx`;
	if [ -n "$NGNIX_PID" ]
	then
		echo -e "\n$COLOR_SUCCESS NGNIX running. $COLOR_RESET"
	else
		echo -e "\n$COLOR_ERROR NGNIX not running. $COLOR_RESET"
	fi
	STYX_PID=`docker ps -q -f name=styx`;
	if [ -n "$STYX_PID" ]
	then
		echo -e "\n$COLOR_SUCCESS STYX running. $COLOR_RESET"
	else
		echo -e "\n$COLOR_ERROR STYX not running. $COLOR_RESET"
	fi
	CHECKITO_PID=`docker ps -q -f name=checkito`;
	if [ -n "$CHECKITO_PID" ]
	then
		echo -e "\n$COLOR_SUCCESS CHECKITO running. $COLOR_RESET"
	else
		echo -e "\n$COLOR_ERROR CHECKITO not running. $COLOR_RESET"
	fi

	BA_PID=`docker ps -q -f name=ba`;
	if [ -n "$BA_PID" ]
	then
		echo -e "\n$COLOR_SUCCESS BookingApp running. $COLOR_RESET"
	else
		echo -e "\n$COLOR_ERROR BookingApp not running. $COLOR_RESET"
	fi
}

########
# HELP #
########

function help {
    echo "Usage: $0 <command> <options>"
    echo "Commands:"
    echo "start                               Start the local environment"
    echo "  -ba <ba-version>                  BA version. Required."
    echo "stop                                Stop the local environment"
    echo "status                              Print the local environment status"
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
	*)
	    help;;
esac
