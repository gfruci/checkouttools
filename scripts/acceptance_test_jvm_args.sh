#!/bin/bash
#
# Collect Bookingapp Acceptance test JVM args for IDE execution

DEBUG="false"
BA_HOME=""
ACC_TEST_DIR_NAME="bookingapp-acceptance-test"
COPY_TO_CLIPBOARD="false"
ENV=dev_rom


function print_help(){
  echo "Usage:"
  echo "${0} [-v] [-c] [-h] [-e env] <bookingapp directory>"
  echo "-v  verbose"
  echo "-c  copy result to clipboard (Windows bash only, it uses /dev/clipboard)"
  echo "-e  acceptance tests env (e.g dev_rom). Default is dev."
  echo "-h  print this help"
}

function debug_log(){
  [[ "${DEBUG}" == "true" ]] && echo "${1}" | awk '{printf("[DEBUG] %s\n", $0)}'
}

function error(){
  echo "${1}" && print_help && exit 1
}

function get_maven_settings_file(){
  local mvn_settings_files=$(mvn -X dummy | egrep "Reading (user|global) settings" | awk '{print $3, $6}')
  local user_settings_file=$(echo "${mvn_settings_files}" | egrep "^user" | awk '{print $2}')
  local global_settings_file=$(echo "${mvn_settings_files}" | egrep "^global" | awk '{print $2}')

  if [ ! -z ${user_settings_file} ]; then
    echo "${user_settings_file}"
  else
    echo "${global_settings_file}"
  fi
}

function parse_maven_settings(){
  cat ${1} \
  | egrep "<(MVT_BUSINESS_CONFIGURATION_HOME|DIONYSUS_UIPACK_HOME)>" \
  | awk -F'[<>]' '{printf("-D%s=%s\n",$2,$3)}'
}

function parse_default_properties(){
  awk '$1' ${1}/${ACC_TEST_DIR_NAME}/src/test/resources/conf/acceptance_test_{${ENV},additional_context_fragments}_system.properties \
  | grep -v '#' | awk '{printf("-D%s\n", $0)}'
}

function parse_pom_xml(){
  cat ${1}/${ACC_TEST_DIR_NAME}/pom.xml \
  | egrep "<(COOKIELESS_DOMAIN_ENABLED|LOCALISATION_DEV_LANGUAGE_TO_LOAD|UI_DEVELOPMENT_MODE_ENABLED)>" \
  | awk -F'[<>]' '{printf("-D%s=%s\n",$2,$3)}'
}

function process_args(){
  while [[ ${#} -gt 0 ]]; do
    case "${1}" in
    -v) DEBUG="true" ;;
    -c) COPY_TO_CLIPBOARD="true" ;;
    -h) print_help && exit 0 ;;
    -e) shift
        ENV=${1} ;;
    *) BA_HOME=${1} ;;
    esac
    shift
  done

}

function main(){
  local settings_from_maven settings_from_properties settings_from_pom_xml
  
  ACC_TEST_DIR_NAME="$(basename ${BA_HOME})-acceptance-test"
  
  echo "Collecting properties..."
  echo -n "1. From maven settings..."
  settings_from_maven=`parse_maven_settings $(get_maven_settings_file)`
  echo " done"
  debug_log "${settings_from_maven}"

  echo -n "2. From project properties file..."
  settings_from_properties=`parse_default_properties ${BA_HOME}`
  echo " done"
  debug_log "${settings_from_properties}"

  echo -n "3. From project pom xml file..."
  settings_from_pom_xml=`parse_pom_xml ${BA_HOME}`
  echo " done"
  debug_log "${settings_from_pom_xml}"

  if [[ "${COPY_TO_CLIPBOARD}" == "true" ]]; then
    echo -e "${settings_from_maven//\\/\\\\}\n${settings_from_properties//\\/\\\\}\n${settings_from_pom_xml//\\/\\\\}" > /dev/clipboard
    echo "Result copied to the clipboard"
  else
    echo -e "\nJVM args:\n"
    echo -e "${settings_from_maven//\\/\\\\}\n${settings_from_properties//\\/\\\\}\n${settings_from_pom_xml//\\/\\\\}"
  fi
}

process_args "${@}"
main
