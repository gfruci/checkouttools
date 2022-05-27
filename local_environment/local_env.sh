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

PROXY_CONFIG="-Dhttp.proxyHost=${DOCKER_GATEWAY_HOST:-host.docker.internal} -Dhttp.proxyPort=8888 -Dhttps.proxyHost=${DOCKER_GATEWAY_HOST:-host.docker.internal} -Dhttps.proxyPort=8888 -DproxyHost=${DOCKER_GATEWAY_HOST:-host.docker.internal} -DproxyPort=8888"

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
DOCKER_IMAGE_PREFIX="kumo-docker-release-local.artylab.expedia.biz/library"
BA_IMAGE_NAME="bookingapp"
BMA_IMAGE_NAME="bookingmanagementapp"
BCA_IMAGE_NAME="bookingchangeapp"

app_cmd() {
    case "$1" in
        "mvt,update_cmd")
            echo "docker pull ${DOCKER_IMAGE_PREFIX}/hcom-mvt:latest >> ${SCRIPT_DIR}/logs/startup.log 2>&1";;

        "styxpres,start_status_cmd")
            echo "grep -i \"Started styx server in\" ${SCRIPT_DIR}/logs/styxpres.log";;
        "styxpres,stop_status_cmd")
            echo "grep -e \"styxpres.*ERROR\" ${SCRIPT_DIR}/logs/styxpres.log | grep -v \"locsClientLoader\"";;
        "styxpres,update_cmd")
            echo "docker pull ${DOCKER_IMAGE_PREFIX}/styxpres:release >> ${SCRIPT_DIR}/logs/startup.log 2>&1";;

        "checkito,start_status_cmd")
            echo "grep \"checkito.*Checkito listening for HTTPS requests\" ${SCRIPT_DIR}/logs/checkito.log";;
        "checkito,stop_status_cmd")
            echo "grep -e \"checkito.*ERROR\" ${SCRIPT_DIR}/logs/checkito.log";;
        "checkito,update_cmd")
            echo "docker pull ${DOCKER_IMAGE_PREFIX}/checkito:[tag] >> ${SCRIPT_DIR}/logs/startup.log 2>&1";;

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
    docker login kumo-docker-release-local.artylab.expedia.biz || echo "You're using windows and git bash and these can not understand some commands. Try to use bash (near to your sh command under git/bin) to run the script"

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

function login-to-aws {
    echo "Starting egctl"
    export AWS_PROFILE=default
    egctl profile bookingapp-local-env || echo "bookingapp-local-env should be configured with egctl, please configure it using the instructions in the readme"
    egctl login
    echo "Your token will expire after 1 hour, please either run \"egctl login\" or restart BA to continue to use EG TnL."
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
				BA_VERSION="${BA_IMAGE_NAME}:latest"
			else
				BA_VERSION="${DOCKER_IMAGE_PREFIX}/${BA_IMAGE_NAME}:${BA_VERSION}"
				echo "Using version: ${BA_VERSION}"
			fi
            login-to-aws
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
		else
			if [ "${BMA_VERSION}" = "local" ]
			then
				echo "Using local build"
				BMA_VERSION="${BMA_IMAGE_NAME}:latest"
			else
				BMA_VERSION="${DOCKER_IMAGE_PREFIX}/${BMA_IMAGE_NAME}:${BMA_VERSION}"
				echo "Using version: ${BMA_VERSION}"
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
		else
			if [ "${BCA_VERSION}" = "local" ]
			then
				echo "Using local build"
				BCA_VERSION="${BCA_IMAGE_NAME}:latest"
			else
				BCA_VERSION="${DOCKER_IMAGE_PREFIX}/${BCA_IMAGE_NAME}:${BCA_VERSION}"
				echo "Using version: ${BCA_VERSION}"
			fi
		fi
    fi

    if [ "${APP}" = "checkito" ]
    then
        if [ "${STUB_STATUS}" = "_no_stub" ]
        then
            return 1
        fi
        export CHECKITO_VERSION="${DOCKER_IMAGE_PREFIX}/checkito:${CHECKITO_VERSION:-latest}"
        export SUIT
    fi

    if [ "${APP}" = "bpe" ]
    then
        if ! ${START_BPE}
        then
            if [ "${START_MODE}" != "start-all" ]
            then
                echo "Error! BPE version NOT specified (missing -bpe-version parameter)!"
            fi
            return 1
        fi
        if ${START_PIO}
        then
          APP_TYPE="_pio_local"
          PIO_CONTAINER_ID=$(docker container ls | grep 'paymentinitializationorchestrator:' | tail -1 | awk -F ' ' '{print $1}')
          if [ -z "$PIO_CONTAINER_ID" ]
          then
            echo "No running PIO container found, cannot start BPE with PIO locally"
            return 1
          fi
          PIO_LOCAL_HOST=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $PIO_CONTAINER_ID)
          export PIO_LOCAL_HOST
        fi
    fi

    if [ "${APP}" = "pio" ]
    then
        if ! ${START_PIO}
        then
            if [ "${START_MODE}" != "start-all" ]
            then
                echo "Error! PIO version NOT specified (missing -pio-version parameter)!"
            fi
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
	echo "Please check out README at https://github.expedia.biz/hotels-checkout/checkouttools/blob/master/local_environment/README.md, it contains several types of error and fixes."
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
    echo "./local_env.sh start [-proxy]                                            Start the local environment, with no front-end apps (BA)"
    echo "./local_env.sh start -ba-version <ba-version> [-no-stub] [-proxy] [-j8] Start the local environment, using the BA version: <ba-version>"
    echo "./local_env.sh start -bma-version <bma-version> [-no-stub] [-proxy]      Start the local environment, using the BMA version: <bma-version>"
    echo "./local_env.sh start -bca-version <bca-version> [-no-stub] [-proxy]      Start the local environment, using the BMA version: <bma-version>"
	  echo "                                                          Use 'local' as version to start up with local built image"
	  echo ""
    echo "./local_env.sh stop                                                      Stop the local environment"
    echo "./local_env.sh status                                                    Print the local environment status"
    echo "./local_env.sh start-app <app_id>                                        Start only the specified app ($(for APP in "${APPS[@]}"; do echo -n " ${APP}"; done) )"
    echo "./local_env.sh stop-app <app_id>                                         Stop only the specified app ($(for APP in "${APPS[@]}"; do echo -n " ${APP}"; done) )"
    echo "./local_env.sh update [<app_id>]                                         Update local environment scripts, along with the specified app ( styxpres chekito mvt )."
    echo "                                                          By default updates styxpres, chekito and mvt docker images"
    echo
    echo "Start environment related services without any application (no checkito):"
    echo "./local-env.sh start -no-stub"
    echo "BA start examples:"
    echo "./local_env.sh start-app ba -ba-version local -no-stub"
    echo "./local_env.sh start-app ba -ba-version bf0538ab789c71793aa2c025400d884813c7bc18 -no-stub"
    echo "./local_env.sh start-app ba -ba-version af092f20f5bc06af679259d6125c7eb8544c6b44-18627 -no-stub"
    echo 
    echo "BMA start examples:"
    echo "./local_env.sh start-app bma -bma-version local -no-stub"
    echo "./local_env.sh start-app bma -bma-version dd95b6bc40cfb4227aa4738236fba516e87df669 -no-stub"
    echo "./local_env.sh start-app bma -bma-version dd95b6bc40cfb4227aa4738236fba516e87df669-18627 -no-stub"
    echo
    echo "Options:"
    echo "-no-stub                                                  Start the local environment without using checkito as mocking server (by default is using Checkito)"
    echo "-proxy                                                    Set the local environment proxy host to docker.for.mac.localhost:8888"
    echo "-j8                                                       Sets Java 8 related options"
    echo "-suit                                                     Configures which suit will be used with checkito"
    echo
    echo "For any other help, please check out README at https://github.expedia.biz/hotels-checkout/checkouttools/blob/master/local_environment/README.md"
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
             -checkito-version)
                export CHECKITO_VERSION=$2
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
