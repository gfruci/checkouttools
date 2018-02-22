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

BA_VERSION=latest
CHECKITO_VERSION=latest

#####################
# UTILITY FUNCTIONS #
#####################

function watch {
    echo -e "\n$COLOR_WATCH $1 $COLOR_RESET"
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
  		sleep 2
	done
}

function docker_login {
    loginResult=$(echo "" | docker pull registry.docker.hcom/hotels/styxpres:release > /dev/null 2>&1)

    if [[ $loginResult = *"unauthorized"* ]]; then
      echo -e "\n$COLOR_HEADER Login to Docker ... $COLOR_RESET"
      docker login registry.docker.hcom
    fi
}

function setup_apps_versions {
    while [[ $# > 0 ]]; do
      case $1 in
        -ba)
          BA_VERSION=$2
          shift
          ;;
        -checkito)
          CHECKITO_VERSION=$2
          shift
          ;;
      esac
      shift
    done

    export BA_VERSION=${BA_VERSION}
    export CHECKITO_VERSION=${CHECKITO_VERSION}
}

###############################
# START/STOP/STATUS FUNCTIONS #
###############################

function start {

    setup_apps_versions;

    docker_login;

    echo -e "\n$COLOR_HEADER Starting local environment ... $COLOR_RESET"

    cd $SCRIPT_DIR
    nohup docker-compose up --no-color > startup.log & 2>&1
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




    #Check for styx start: "Started styx server in"

    #ERROR: for checkito  Cannot start service checkito: driver failed programming external connectivity on endpoint localenvironment_checkito_1 (2cad1cb74ec95dc56ca8ae12bbc51d9a7400f92494f94ca4efb6c77c80efc446): Error starting userland proxy: Bind for 0.0.0.0:8189 failed: port is already allocated
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
    echo "  -ba <ba-version>                  default: latest"
    echo "  -checkito <checkito-version>      default: latest"
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
