#/bin/bash

if [[ $# -eq 0 || $# > 2 ]] ; then
    echo '------------------------------------------------------------------------------------'
    echo '| No argument or too much arguments supplied.                                      |'
    echo '|                                                                                  |'
    echo '| Usage:                                                                           |'
    echo '|    ./get_certificates_from_venafi.sh <service_account> <service_account_pwd>     |'
    echo '|                                                                                  |'
    echo '------------------------------------------------------------------------------------'
    exit 1
fi

SCRIPT="`basename -- $0`"
ENVS=("egdp-test" "egdp-prod")
SERVICE_ACCOUNT="$1"
SERVICE_ACCOUNT_PASSWORD="$2"
TRUSTSTORE_PASSWORD="$2"
KEYSTORE_PASSWORD="$2"
KEY_PASSWORD="$2"
TOKEN_CACHE="./.venafi_token"

error_exit() {
    echo "${SCRIPT}: ${1:-"Unknown Error"}"
    exit 1
}

fetch_new_token() {
  TMP_TOKEN_CACHE="`mktemp -t venafi-XXXXXXXXXX`"
  curl -k -s -X POST \
                -H "Content-Type: application/json" \
                -d "{
                    \"Username\":\"$SERVICE_ACCOUNT\",
                    \"Password\":\"$SERVICE_ACCOUNT_PASSWORD\"
                }" \
                https://certs.sea.corp.expecn.com/vedsdk/authorize/ -o $TMP_TOKEN_CACHE
  cat $TMP_TOKEN_CACHE | jq -r '.' 2>&1 1>/dev/null && mv $TMP_TOKEN_CACHE $TOKEN_CACHE
  TOKEN="`cat $TOKEN_CACHE | jq -r '.APIKey'`"
}

fetch_new_token

[ -z $TOKEN ] && error_exit "Token is empty"

for env in "${ENVS[@]}"
do
  echo "Getting keystore for $env..."
  FILE_NAME="output-$env.txt"
  echo > $FILE_NAME
  echo "-------------------------------------- keystore_base64 ---------------------------------------" >> $FILE_NAME
  
  KEYSTORE=$(curl -k -s -L -X POST \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d "{
                \"CertificateDN\":\"\\\\VED\\\\Policy\\\\EG Custom\\\\StreamPlatform\\\\$env\\\\customers\\\\$SERVICE_ACCOUNT\\\\$SERVICE_ACCOUNT.$env.lcl\",
                \"Format\":\"JKS\",
                \"IncludeChain\":\"false\",
                \"RootFirstOrder\":\"true\",
                \"FriendlyName\":\"$SERVICE_ACCOUNT.$env.lcl\",
                \"KeystorePassword\":\"$KEYSTORE_PASSWORD\",
                \"IncludePrivateKey\":\"true\",
                \"Password\": \"$KEY_PASSWORD\"
            }" \
        https://certs.sea.corp.expecn.com/vedsdk/certificates/retrieve \
    | jq -r '.CertificateData')
 
  [ -z $KEYSTORE ] && error_exit "Keystore is empty"

  echo "Keystore donwloaded with success for $env..."
  echo "$KEYSTORE" >> $FILE_NAME
  echo "----------------------------------------------------------------------------------------------" >> $FILE_NAME
  echo >> $FILE_NAME
  echo "-------------------------------------- truststore_base64 -------------------------------------" >> $FILE_NAME

  echo "Getting truststore for $env..."
  TRUSTSTORE=$(curl -k -s -L -X POST \
          -H "Content-Type: application/json" \
          -H "Authorization: Bearer $TOKEN" \
          -d "{
                  \"CertificateDN\":\"\\\\VED\\\\Policy\\\\EG Custom\\\\StreamPlatform\\\\$env\\\\customers\\\\$SERVICE_ACCOUNT\\\\$SERVICE_ACCOUNT.$env.lcl\",
                  \"Format\":\"JKS\",
                  \"IncludeChain\":\"true\",
                  \"RootFirstOrder\":\"true\",
                  \"FriendlyName\":\"$SERVICE_ACCOUNT.$env.lcl\",
                  \"KeystorePassword\":\"$TRUSTSTORE_PASSWORD\",
                  \"IncludePrivateKey\":\"false\"
              }" \
          https://certs.sea.corp.expecn.com/vedsdk/certificates/retrieve \
      | jq -r '.CertificateData')
   
   
  [ -z $TRUSTSTORE ] && error_exit "Truststore is empty"

  echo "Truststore donwloaded with success for $env..."
  echo "$TRUSTSTORE" >> $FILE_NAME
  echo "----------------------------------------------------------------------------------------------" >> $FILE_NAME
done