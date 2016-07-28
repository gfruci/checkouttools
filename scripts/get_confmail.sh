#!/bin/bash
query="{\"method\": \"POST\",\"urlPattern\": \".*/event_service/mf/confirmationEmail-3.0\"}"


curl -s -X POST -d "$query" http://localhost:8090/__admin/requests/find | grep body | perl -pe 's|.*HTML__HtmlBody<\/ns4:Name><ns4:Value>(.*?)<\/.*|\1|' | base64 -d

if [ "-r" == "$1" ]; then
  echo -e "\n\nClear requests..."
  curl -s -X POST -d "" http://localhost:8090/__admin/requests/reset
  echo "done"
fi
