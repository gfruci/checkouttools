# ROKT public key converter

A small Java application to convert a public key from JSON exponent/modulus format into PEM format

BMA uses the resulting public key for encrypting when sending PII fields to ROKT

Owner: CKO/CCAT, slack: #hcom-cko-ccat-sup

### How it works
  - ROKT provides to HCOM two JSON files which contain the actual public key exponent and modulus values for the staging and production environments. Per
    agreement as of 2018.09 this happens once a year
  - HCOM feeds the received JSON files to this key converter application and creates two PEM files containing the public keys for the staging and production
    environments respectively
  - HCOM replaces the existing PEM files with the newly created PEM files in the BMA application repository:
    https://stash.hcom/projects/COP/repos/bookingmanagementapp/browse/bookingmanagementapp-web/src/main/resources/com/hotels/booking/bookingdetails/tracking/udt
  - HCOM updates the value of the ```ROKT_PUBLIC_KEY_LOCATION``` variable in the concerned BMA environment property files
    * https://stash.hcom/projects/COP/repos/bookingmanagementapp/browse/bookingmanagementapp-web/src/main/resources/conf/environment/env_rules_default.properties for staging environment
    * https://stash.hcom/projects/COP/repos/bookingmanagementapp/browse/bookingmanagementapp-web/src/main/resources/conf/environment/env_rules_production.properties for production environment

### Build and run
  - Run the application in your preferred way (IDE, command line etc.) with the full path to the input JSON file supplied as a program argument 
    (e.g. ```c:\Users\Your_UserName\prod.pubkey```). The application should be run
    two times, once for the staging and once for the production key!
  - The output PEM file will be created in the same directory where the input JSON file is located, named simply ```$INPUT_FILE_NAME + .pem``` (e.g. if the name of the input JSON file name was
    ```prod.pubkey```, then the output PEM file name will be ```prod.pubkey.pem```
  - Rename the output PEM files manually into the naming format expected by BMA. The currently used name formats are ```RoktPublicKeyStagingYYYYMM.pem```
    and ```RoktPublicKeyProductionYYYYMM.pem```, where ```YYYYMM``` stands for actual year and month, e.g. ```RoktPublicKeyStaging201809.pem``` and
    ```RoktPublicKeyProduction201809.pem```
  - Copy the properly named PEM files into the BMA application repository and update the the environment property files as described above

