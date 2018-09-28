# ROKT public key generator

A small Java application to generate the public keys for BMA encrypting the PII fields to be sent to ROKT

Owner: CKO/CCAT, slack: #hcom-cko-ccat-sup

### How it works
  - ROKT provides to HCOM two JSON files which contain the actual public key exponent and modulus values for staging and production environments
    Per agreement as of 2018.09 this happens once a year
  - HCOM feeds the received JSON files to this key generator app which creates two PEM files containing the public keys for the staging and
    production environments respectively
  - HCOM replaces the existing PEM files with the newly generated PEM files in the BMA application repository:
    https://stash.hcom/projects/COP/repos/bookingmanagementapp/browse/bookingmanagementapp-web/src/main/resources/com/hotels/booking/bookingdetails/tracking/udt
  - HCOM updates the value of the ROKT_PUBLIC_KEY_LOCATION variable in the concerned BMA environment property files
    -- https://stash.hcom/projects/COP/repos/bookingmanagementapp/browse/bookingmanagementapp-web/src/main/resources/conf/environment/env_rules_default.properties for staging environment
    -- https://stash.hcom/projects/COP/repos/bookingmanagementapp/browse/bookingmanagementapp-web/src/main/resources/conf/environment/env_rules_production.properties for production environment

### Configuration
  - The currently used name formats for the PEM files in BMA are ```RoktPublicKeyStagingYYYYMM.pem``` and ```RoktPublicKeyProductionYYYYMM.pem```,
    where ```YYYYMM``` stands for actual year and month, e.g. ```RoktPublicKeyStaging201809.pem``` and ```RoktPublicKeyProduction201809.pem```
  - The key generator application has been prepared to create the PEM files in this name format, and expects the input JSON files to be
    named accordingly, e.g. ```RoktPublicKeyStaging201809.json``` and ```RoktPublicKeyProduction201809.json```
  - The input JSON files are assumed to be put in module root directory: https://stash.hcom/projects/COP/repos/checkouttools/browse/rokt_public_key_generator
  - The application expects the ```YYYYMM``` format timestamp as a program argument

### Build and run
  - If necessary, rename the input JSON files received from ROKT to the above described name format and place them into the module root directory
  - Run the application in your preferred way (IDE, command line etc.) with the ```YYYYMM``` format timestamp supplied as a program argument
  - The output PEM files should be created in the module root directory
