#!/bin/bash
#
# Return the last BA version tag from Git.

BA_HOME="$(dirname "$(dirname "$(pwd)")")/bookingapp"

function print_help(){
  echo "Usage:"
  echo "${0} [-e env]"
  echo "-ba  set BA directory. Default is ${BA_HOME}."
  echo "-h   print this help"
}

function process_args(){
  while [[ ${#} -gt 0 ]]; do
    case "${1}" in
    -h) print_help && exit 0 ;;
    -ba) shift
        BA_HOME=${1} ;;
    esac
    shift
  done
}

function check_if_BA_home_dir_exists() {
  if [ ! -d "$BA_HOME" ]; then
    echo "BA home directory not found in \"${BA_HOME}\". Exiting."
    exit 1
  fi
}

function fetch_tags_and_get_version(){
  cd ${BA_HOME}
  git fetch --tags
  git tag | grep -E "^BA\.1[0-9]{2}\.[0-9]+\.[0-9]{4}$" | tail -1
}

process_args "${@}"
check_if_BA_home_dir_exists
fetch_tags_and_get_version
