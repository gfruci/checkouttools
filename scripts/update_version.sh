#!/bin/bash

BASEDIR=""
DESIRED_VERSION=""
PUSH=false

function print_help(){
  echo "Usage:"
  echo "${0} [-h] [-p] [-v version] <app directory>"
  echo "Update version for maven projects."
  echo "-v  set specific version, default is increasing the major version"
  echo "-p  push changes to master"
  echo "-h  print this help"
  echo ""
  echo "Examples:"
  echo "  ${0} /path/to/bookingchangeapp                        Increase the major version of bookingchangeapp."
  echo "  ${0} -p -v 66.6-SNAPSHOT /path/to/bookingchangeapp    Set the version of bookingchangeapp to 66.6-SNAPSHOT and push the changes to master."
}

function process_args(){
  while [[ ${#} -gt 0 ]]; do
    case "${1}" in
    -h) print_help && exit 0 ;;
	-p) PUSH="true" ;;
    -v) shift
        DESIRED_VERSION=${1} ;;
    *) BASEDIR=${1} ;;
    esac
    shift
  done
}

function check_if_basedir_exists() {
  if [ ! -d "$BASEDIR" ]; then
    echo "No directory found in \"${BASEDIR}\". Exiting."
    exit 1
  fi
  # Go to BASEDIR
  cd $BASEDIR
  echo "Updating ${PWD##*/} version"
}

function stash_changes_and_get_master(){
  # Store the current branch name
  BRANCH=$(git branch | grep \* | cut -d ' ' -f2)
  
  # Put everything to stash
  git add .
  ANYTHING_STASHED="$(git stash)"
  
  # Get a fresh master
  if [ "$BRANCH" != "master" ]; then
    git checkout master
  fi
  git pull
}

function update_version(){
  # Get the current version from maven
  VERSION=$(printf 'VER\t${project.version}' | mvn help:evaluate | grep '^VER' | cut -f2 | cut -f1 -d".")
  
  if [ "$DESIRED_VERSION" != "" ]; then
    NEW_VERSION_STRING=$DESIRED_VERSION
  else
	# Increase the major version
	((NEW_VERSION=VERSION+1))
	NEW_VERSION_STRING=$NEW_VERSION.0-SNAPSHOT
  fi
  
  # Update the version with maven
  mvn versions:set -DnewVersion=$NEW_VERSION_STRING
}

function commit_and_push_changes(){
  git add ./\*.xml
  git commit -m "@noissue: Version updated to $NEW_VERSION_STRING"
  if [ $PUSH != "true" ]; then
    echo "The changes below won't be pushed to master. Exiting..."
	git status
    exit 0
  fi
  git push
}

function get_back_changes_from_stash(){
  # Switch back to original branch
  git checkout $BRANCH
  
  if [ "No local changes to save" != "$ANYTHING_STASHED" ]; then
  	git stash pop
  fi
}

process_args "${@}"
check_if_basedir_exists
stash_changes_and_get_master
update_version
commit_and_push_changes
get_back_changes_from_stash
