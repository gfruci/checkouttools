#!/usr/bin/env bash

export MSYS_NO_PATHCONV=1
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

if [[ "$OSTYPE" == "darwin" ]]; then
    PROXY_CONFIG="-Dhttp.proxyHost=docker.for.mac.localhost -Dhttp.proxyPort=8888 -Dhttps.proxyHost=docker.for.mac.localhost -Dhttps.proxyPort=8888 -DproxyHost=docker.for.mac.localhost -DproxyPort=8888"
elif [[ "$OSTYPE" == "win32" ]]; then
    PROXY_CONFIG="-Dhttp.proxyHost=docker.for.win.localhost -Dhttp.proxyPort=8888 -Dhttps.proxyHost=docker.for.win.localhost -Dhttps.proxyPort=8888 -DproxyHost=docker.for.win.localhost -DproxyPort=8888"
elif [[ "$OSTYPE" == "msys" ]]; then
    PROXY_CONFIG="-Dhttp.proxyHost=docker.for.win.localhost -Dhttp.proxyPort=8888 -Dhttps.proxyHost=docker.for.win.localhost -Dhttps.proxyPort=8888 -DproxyHost=docker.for.win.localhost -DproxyPort=8888"
else
    PROXY_CONFIG="-Dhttp.proxyHost=docker.for.mac.localhost -Dhttp.proxyPort=8888 -Dhttps.proxyHost=docker.for.mac.localhost -Dhttps.proxyPort=8888 -DproxyHost=docker.for.mac.localhost -DproxyPort=8888"
fi

START_MODE=
BA_VERSION=
BMA_VERSION=
BCA_VERSION=
export PIO_VERSION="latest"  # N.B.: export only marks variables for automatic export
export BPE_VERSION="latest"
START_BPE=false
START_PIO=false
STUB_STATUS=
SUIT="default"
TRUSTSTORE_PATH="/hcom/share/java/default/lib/security/cacerts_plus_internal"
DEBUG_OPTS="-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:1901"
export ORIGINS_PATH="/styxconf/origins.yaml"

APPS=( "mvt" "ba" "bma" "bca" "pio" "bpe" "checkito" "styxpres" "nginx")

app_cmd() {
    case "$1" in
        "mvt,update_cmd")
            echo "docker pull kumo-docker-release-local.artylab.expedia.biz/library/hcom-mvt:latest >> ${SCRIPT_DIR}/logs/startup.log 2>&1";;

        "styxpres,start_status_cmd")
            echo "grep -i \"Started styx server in\" ${SCRIPT_DIR}/logs/styxpres.log";;
        "styxpres,stop_status_cmd")
            echo "grep -e \"styxpres.*ERROR\" ${SCRIPT_DIR}/logs/styxpres.log | grep -v \"locsClientLoader\"";;
        "styxpres,update_cmd")
            echo "docker pull kumo-docker-release-local.artylab.expedia.biz/library/styxpres:release >> ${SCRIPT_DIR}/logs/startup.log 2>&1";;

        "checkito,start_status_cmd")
            echo "grep \"checkito.*Checkito listening for HTTPS requests\" ${SCRIPT_DIR}/logs/checkito.log";;
        "checkito,stop_status_cmd")
            echo "grep -e \"checkito.*ERROR\" ${SCRIPT_DIR}/logs/checkito.log";;
        "checkito,update_cmd")
            echo "docker pull kumo-docker-release-local.artylab.expedia.biz/library/checkito:[tag] >> ${SCRIPT_DIR}/logs/startup.log 2>&1";;

        "nginx,start_status_cmd")
            echo "grep -e \"nginx.*done\" ${SCRIPT_DIR}/logs/nginx.log";;
        "nginx,stop_status_cmd")
            echo "grep -e \"nginx.*error\" ${SCRIPT_DIR}/logs/nginx.log";;

        "ba,start_status_cmd")
            echo "grep \"ba.*Server startup\" ${SCRIPT_DIR}/logs/ba.log";;
        "ba,stop_status_cmd")
            echo "grep -e \"ba.*ERROR \" ${SCRIPT_DIR}/logs/ba.log | grep -v \"locsClientLoader\"";;

        "bma,start_status_cmd")
            echo "grep \"bma.*Server startup\" ${SCRIPT_DIR}/logs/bma.log";;
        "bma,stop_status_cmd")
            echo "grep -e \"bma.*ERROR\" ${SCRIPT_DIR}/logs/bma.log | grep -v \"locsClientLoader\"";;

        "bca,start_status_cmd")
            echo "grep \"bca.*Server startup\" ${SCRIPT_DIR}/logs/bca.log";;
        "bca,stop_status_cmd")
            echo "grep -e \"bca.*ERROR\" ${SCRIPT_DIR}/logs/bca.log | grep -v \"locsClientLoader\|ConfigurationReloadSupport\"";;

        "pio,start_status_cmd")
            echo "grep \"pio.*Started ServiceApplication\" ${SCRIPT_DIR}/logs/pio.log";;
        "pio,stop_status_cmd")
            echo "grep -e \"pio.*ERROR\" ${SCRIPT_DIR}/logs/pio.log";;

        "bpe,start_status_cmd")
            echo "grep \"bpe.*Started ServiceApplication\" ${SCRIPT_DIR}/logs/bpe.log";;
        "bpe,stop_status_cmd")
            echo "grep \"bpe.*ERROR\" ${SCRIPT_DIR}/logs/bpe.log";;

        *)
            echo "";;
    esac
}

#####################
# UTILITY FUNCTIONS #
#####################

function watch {
    echo -e "\n$COLOR_WATCH $1 $COLOR_RESET"

    timeStart=`date +%s`

    while :
    do
        eval $2 > /dev/null
        if [ $? -eq 0 ]
        then
            return 0
        else
            ERROR=$(eval $3)
            if [ $? -eq 0 ]
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

function login {
    docker login kumo-docker-release-local.artylab.expedia.biz

    if [ $? -eq 1 ]; then
        echo -e "\n$COLOR_ERROR Docker login failed! $COLOR_RESET"
        exit 1
    fi
}

function update {
    login

    echo -e "\n$COLOR_HEADER Updating local environment ... $COLOR_RESET"

    echo "Updating scripts ..."
    cd ${SCRIPT_DIR}
    git pull
    cd ${PREV_DIR}

    APP=$1

    if [ "${APP}" != "" ]; then
        APPS=( "${APP}" )
    fi

    for APP in "${APPS[@]}"
    do
        UDPATE_CMD=$(app_cmd "${APP},update_cmd")
        if [ -n "${UDPATE_CMD}" ]
        then
            echo "Updating ${APP} ..."
            $(eval ${UDPATE_CMD})
        fi
    done

    echo "done"
}

###############################
# START/STOP/STATUS FUNCTIONS #
###############################

function start-app {

    export DEBUG_OPTS
    export TRUSTSTORE_PATH
    APP=$1
    APP_TYPE=""

    echo "" > ${SCRIPT_DIR}/logs/${APP}.log

    if [ "${APP}" = "ba" ]
    then
        APP_TYPE=${STUB_STATUS}
        if [ "${BA_VERSION}" = "" ]
        then
            if [ "${START_MODE}" = "start-all" ]
            then
                return 1
            else
                echo "Error! BA version NOT specified (missing -ba-version parameter)!"
                help
                exit 1
            fi
		else
			if [ "${BA_VERSION}" = "local" ]
			then
				echo "Using local build"
				BA_VERSION="bookingapp:latest"
			else
				BA_VERSION="kumo-docker-release-local.artylab.expedia.biz/library/bookingapp:${BA_VERSION}"
				echo "Using ba version: ${BA_VERSION}"
			fi
		fi
    fi

    if [ "${APP}" = "bma" ]
    then
        APP_TYPE=${STUB_STATUS}
        if [ "${BMA_VERSION}" = "" ]
        then
            if [ "${START_MODE}" = "start-all" ]
            then
                return 1
            else
                echo "Error! BMA version NOT specified (missing -bma-version parameter)!"
                help
                exit 1
            fi
        fi
    fi

    if [ "${APP}" = "bca" ]
    then
        APP_TYPE=${STUB_STATUS}
        if [ "${BCA_VERSION}" = "" ]
        then
            if [ "${START_MODE}" = "start-all" ]
            then
                return 1
            else
                echo "Error! BCA version NOT specified (missing -bca-version parameter)!"
                help
                exit 1
            fi
        fi
    fi

    if [ "${APP}" = "checkito" ]
    then
        if [ "${STUB_STATUS}" = "_no_stub" ]
        then
            return 1
        fi

        export SUIT
    fi

    if [ "${APP}" = "bpe" ]
    then
        if ! ${START_BPE} && ! ${START_PIO}
        then
            return 1
        fi
        if ! ${START_BPE} && [ "${START_MODE}" != "start-all" ]
        then
            echo "Error! BPE version NOT specified (missing -bpe-version parameter)!"
            return 1
        fi
        PIO_CONTAINER_ID=$(docker container ls | grep '/pio:' | tail -1 | awk -F ' ' '{print $1}')
        PIO_LOCAL_HOST=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $PIO_CONTAINER_ID)
        export PIO_LOCAL_HOST
    fi

    if [ "${APP}" = "pio" ]
    then
        if ! ${START_BPE} && ! ${START_PIO}
        then
            return 1
        fi
        if ! ${START_PIO} && [ "${START_MODE}" != "start-all" ]
        then
            echo "Error! PIO version NOT specified (missing -pio-version parameter)!"
            return 1
        fi
        APP_TYPE=${STUB_STATUS}
    fi

    cd ${SCRIPT_DIR}
    nohup docker-compose up --no-color ${APP}${APP_TYPE} >> ${SCRIPT_DIR}/logs/${APP}.log 2>&1 &
    cd ${PREV_DIR}

    START_STATUS_CMD=$(app_cmd "${APP},start_status_cmd")
    STOP_STATUS_CMD=$(app_cmd "${APP},stop_status_cmd")
    watch "Starting ${APP} ..." "$START_STATUS_CMD" "$STOP_STATUS_CMD"
    WATCH_RETURN_CODE=$?

    if [ ${WATCH_RETURN_CODE} -eq 0 ]
    then
        echo -e "\n$COLOR_SUCCESS ${APP} started $COLOR_RESET"
        type terminal-notifier &>/dev/null && terminal-notifier -title "LESS" -message "✅ ${APP} started" -sound 'default' -sender "com.apple.launchpad.launcher"
    else
        echo -e "\n$COLOR_ERROR Error: ${APP} start error $COLOR_RESET"
        type terminal-notifier &>/dev/null && terminal-notifier -title "LESS" -message "📛 ${APP} start error" -sound 'default' -sender "com.apple.launchpad.launcher"
        if [ "${START_MODE}" = "start-all" ]
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

function check_update {
    echo -e "\n$COLOR_HEADER Checking updates for local environment scripts ... $COLOR_RESET"

    cd ${SCRIPT_DIR}
    git fetch >> ${SCRIPT_DIR}/logs/startup.log 2>&1
    git status | grep "origin/master"
    cd ${PREV_DIR}

    echo "Info: Run the 'update' command to update the local environment"
}

function start {
    check_update
    login

    START_MODE="start-all"

    echo "" > ${SCRIPT_DIR}/logs/startup.log

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

    status

    exit 0
}

function status {
    echo -e "\n$COLOR_HEADER Status $COLOR_RESET"

    for APP in "${APPS[@]}"
    do
        PID=`docker ps -q -f name=${APP}`
        if [ -n "$PID" ]
        then
            STATUS_CMD=$(app_cmd "${APP},status_cmd")
            echo -e "\n$COLOR_SUCCESS ${APP} running. $(eval $STATUS_CMD) $COLOR_RESET"
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
    echo "start [-proxy]                                            Start the local environment, with no front-end apps (BA)"
    echo "start -ba-version <ba-version> [-no-stub] [-proxy] [-j8] Start the local environment, using the BA version: <ba-version>"
    echo "start -bma-version <bma-version> [-no-stub] [-proxy]      Start the local environment, using the BMA version: <bma-version>"
    echo "start -bca-version <bca-version> [-no-stub] [-proxy]      Start the local environment, using the BMA version: <bma-version>"
    echo "stop                                                      Stop the local environment"
    echo "status                                                    Print the local environment status"
    echo "start-app <app_id>                                        Start only the specified app ($(for APP in "${APPS[@]}"; do echo -n " ${APP}"; done) )"
    echo "stop-app <app_id>                                         Stop only the specified app ($(for APP in "${APPS[@]}"; do echo -n " ${APP}"; done) )"
    echo "update [<app_id>]                                         Update local environment scripts, along with the specified app ( styxpres chekito mvt )."
    echo "                                                          By default updates styxpres, chekito and mvt docker images"
    echo
    echo "Options:"
    echo "-no-stub                                                  Start the local environment without using checkito as mocking server (by default is using Checkito)"
    echo "-proxy                                                    Set the local environment proxy host to docker.for.mac.localhost:8888"
    echo "-j8                                                       Sets Java 8 related options"
    echo "-suit                                                     Configures which suit will be used with checkito"
    exit 0
}

########
# INIT #
########

function init {
    while [[ $# > 0 ]]; do
        case $1 in
            -ba-version)
                export BA_VERSION=$2
                shift
                ;;
            -no-stub)
                STUB_STATUS=_no_stub
                ;;
            -bma-version)
                export BMA_VERSION=$2
                shift
                ;;
            -bca-version)
                export BCA_VERSION=$2
                shift
                ;;
            -proxy)
                export PROXY_CONFIG
                ;;
            -suit)
                export SUIT=$2
                shift
                ;;
            -j8)
                DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1901"
                TRUSTSTORE_PATH="/hcom/share/java/default/jre/lib/security/cacerts_plus_internal"
                ;;
            -pio-version)
                export PIO_VERSION=$2
                export ORIGINS_PATH="/styxconf/origins_bpe_localhost.yaml"
                START_PIO=true
                ;;
            -bpe-version)
                export BPE_VERSION=$2
                export ORIGINS_PATH="/styxconf/origins_bpe_localhost.yaml"
                START_BPE=true
        esac
        shift
    done
}

init $@

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
    update)
        shift
        update $@;;
    *)
        help;;
esac
