# Checkout Local Environment

The CKO local environment aims to simplify development and testing of checkout FE apps in local.

It leverage the [local-app-server](http://stash.hcom/projects/STYX/repos/local-app-server/browse) to serve the CKO FE apps HTML pages using the dionysus styx plugin.

![local_env](assets/local_env_arch.png)

**Note:** In order to enable styx html page rendering you need to set MVT variant 4418.1

#### Stubbed hotels and supported features:

Please refer to the checkito README for a full list of stubbed hotels and supported features: http://stash.hcom/projects/COP/repos/checkito/browse

#### Limitations

TBW

## Setup

### Windows pre-requirements

To facilitate the maintainance, there is a single (_bash_) script to set up the environment. 

To run it on Windows, *Git BASH* is strongly recommended. It can be downloaded form https://gitforwindows.org/.
If you're already using another BASH emulation tool you can use that one as well. 

During the installation few setup choice have to made. If you're not sure what to select, keep the default choice.  

### Install Docker CE (Mac/Win)

Download, install and launch Docker CE **17.09.x**

* WIN: https://docs.docker.com/docker-for-windows/release-notes/#docker-community-edition-17091-ce-win42-2017-12-11  
* MAC: https://docs.docker.com/docker-for-mac/release-notes/#docker-community-edition-17091-ce-mac42-2017-12-11 

**Important**: DO NOT UPGRADE. The hcom docker registry is not compatible with newer versions.

![do not upgrade](assets/do_not_upgrade.png)

*[Windows]*: After the installation, share the drive on which the local environment folder will be checked out: `Docker -> Settings -> Shared Drives`
*[Windows]*: Add `C:\Program Files\Docker\Docker\Docker for Windows.exe` to PATH env variables (both system and profile):

![do not WIN_docker_exe_to_env_variables](assets/WIN_docker_exe_to_env_variables.png)


**Note:** If your are migrating from Docker Toolbox you will be asked to import your existing docker machine.
In case you have any docker image that you want to keep, select "Copy" - Otherwise "Skip"

![copy docker machine](assets/copy_docker_machine.png)

### Migrating from Docker Toolbox

If you already have an installation of Docker Toolbox and you want to upgrade you need to remove the Docker ENV variables:
1. Close all your terminal windows
2. Remove any docker related environment variable that you have possibly set in any of your profile file (.bashrc, .profile, .bash_profile)

For more details on how to migrate from Docker Toolbox read these:
* https://docs.docker.com/docker-for-mac/docker-toolbox/
* https://wiki.hcom/display/HTS/Upgrading+to+Docker+for+Mac

### Mark internal registries as "Insecure"

Add the insecure internal registries in `Docker -> Preferences -> Daemon -> Basic:`
* registry.docker.hcom
* registry.prod.hcom

![add insecure registries](assets/add_insecure_registries.png)

### Disable legacy registry

add the disable-legacy-registry flag set to false in `Docker -> Preferences -> Daemon -> Advanced`

![disable legacy registry](assets/disable_legacy_registry.png)

*Note*: If you forgot to add the `disable-legacy-registry flag you may get the following error:

    Error response from daemon: login attempt to http://registry.docker.hcom/v2/ failed with status: 500 Internal Server Error

### Login to the registry

Login to registry using your SEA credentials

    docker login registry.docker.hcom
----
*[WINDOWS]*: 
If you're using Git BASH and you get the following error: 

    Error: Cannot perform an interactive login from a non TTY device

you need to use the following command to login:

    winpty docker login registry.docker.hcom
----

### Increase the docker resources

In `Docker -> Preferences -> Advanced`
* increase the docker memory to 4GB
* Increase the number of CPU to 6

![increase_docker_resources](assets/increase_docker_resources.png)

### hosts file

Please update your hosts file with the following.

*Note:* If you already have the `*.dev-hotels.com` domains in your hosts file, you need to substitute them. 

`hosts` file location:
* MAC/UNIX: `/etc/hosts`
* WIN: `C:\Windows\System32\drivers\etc`

```
127.0.0.1 dev-hotels.com
127.0.0.1 en.dev-hotels.com
127.0.0.1 www.dev-hotels.com ssl.dev-hotels.com
127.0.0.1 www.dev-hotels.ca ssl.dev-hotels.ca
127.0.0.1 www.dev-hoteles.com ssl.dev-hoteles.com
127.0.0.1 www.dev-hoteis.com ssl.dev-hoteis.com
127.0.0.1 www.dev-hotels.com ssl.dev-hotels.com
127.0.0.1 us.dev-hotels.com ssl-us.dev-hotels.com
127.0.0.1 uk.dev-hotels.com ssl-uk.dev-hotels.com
127.0.0.1 fr.dev-hotels.ca ssl-fr.dev-hotels.ca
127.0.0.1 ar.dev-hotels.com ssl-ar.dev-hotels.com
127.0.0.1 cs.dev-hotels.com ssl-cs.dev-hotels.com
127.0.0.1 da.dev-hotels.com ssl-da.dev-hotels.com
127.0.0.1 de.dev-hotels.com ssl-de.dev-hotels.com
127.0.0.1 el.dev-hotels.com ssl-el.dev-hotels.com
127.0.0.1 et.dev-hotels.com ssl-et.dev-hotels.com
127.0.0.1 fi.dev-hotels.com ssl-fi.dev-hotels.com
127.0.0.1 fr.dev-hotels.com ssl-fr.dev-hotels.com
127.0.0.1 he.dev-hotels.com ssl-he.dev-hotels.com
127.0.0.1 hr.dev-hotels.com ssl-hr.dev-hotels.com
127.0.0.1 hu.dev-hotels.com ssl-hu.dev-hotels.com
127.0.0.1 is.dev-hotels.com ssl-is.dev-hotels.com
127.0.0.1 it.dev-hotels.com ssl-it.dev-hotels.com
127.0.0.1 jp.dev-hotels.com ssl-jp.dev-hotels.com
127.0.0.1 kr.dev-hotels.com ssl-kr.dev-hotels.com
127.0.0.1 lt.dev-hotels.com ssl-lt.dev-hotels.com
127.0.0.1 lv.dev-hotels.com ssl-lv.dev-hotels.com
127.0.0.1 ms.dev-hotels.com ssl-ms.dev-hotels.com
127.0.0.1 nl.dev-hotels.com ssl-nl.dev-hotels.com
127.0.0.1 no.dev-hotels.com ssl-no.dev-hotels.com
127.0.0.1 pl.dev-hotels.com ssl-pl.dev-hotels.com
127.0.0.1 ru.dev-hotels.com ssl-ru.dev-hotels.com
127.0.0.1 sk.dev-hotels.com ssl-sk.dev-hotels.com
127.0.0.1 sv.dev-hotels.com ssl-sv.dev-hotels.com
127.0.0.1 th.dev-hotels.com ssl-th.dev-hotels.com
127.0.0.1 tr.dev-hotels.com ssl-tr.dev-hotels.com
127.0.0.1 uk.dev-hotels.com ssl-uk.dev-hotels.com
127.0.0.1 zh.dev-hotels.com ssl-zh.dev-hotels.com
127.0.0.1 pt.dev-hoteles.com ssl-pt.dev-hoteles.com
127.0.0.1 cn.dev-hotels.com ssl-cn.dev-hotels.com ssl.dev-hotels.cn
127.0.0.1 es.dev-hotels.com ssl-es.dev-hotels.com
127.0.0.1 ca.dev-hotels.com ssl-ca.dev-hotels.com
127.0.0.1 nz.dev-hotels.com ssl-nz.dev-hotels.com
127.0.0.1 in.dev-hotels.com ssl-in.dev-hotels.com
127.0.0.1 iw.dev-hotels.com ssl-iw.dev-hotels.com
127.0.0.1 www.dev-hoteis.com br.dev-hoteis.com ssl.dev-hoteis.com pt.dev-hotels.com
127.0.0.1 www.dev-hoteles.com mx.dev-hoteles.com ssl.dev-hoteles.com
127.0.0.1 tw.dev-hotels.com ssl-tw.dev-hotels.com

#AFFILIATES
127.0.0.1 hotels.dev-united.com ssl.dev-united.com
127.0.0.1 hotels.dev-latam.com ssl.dev-latam.com
127.0.0.1 hotels.dev-multiplus.com ssl.dev-mulitplus.com
127.0.0.1 hotels.dev-hotelurbano.com ssl.dev-hotelurbano.com
127.0.0.1 hotels.dev-hcombest.com ssl.dev-hcombest.com

#CHECKITO
127.0.0.1 checkito.hcom checkito
```

### Checkout the local environment

The local environment scripts and assets are in the git `checkouttools` repo.
Clone the repo.

    $ cd <workspace_folder>
    $ git clone http://<sea_username>@stash.hcom/scm/cop/checkouttools.git

## Usage

### Start/Stop

Move under the `local_environment`, give execution permissions to the `local_env.sh` bash script and run it.

    $ cd <local_environment_root_folder>
    $ chmod a+x local_env.sh 
    $ ./local_env.sh
    Usage: /usr/local/bin/local_env <command> <options>
    Commands:
    start -ba-version <ba-version> [-no-stub]     Start the local environment, using the BA version: <ba-version>
    stop                                          Stop the local environment
    status                                        Print the local environment status
    start-app <app_id>                            Start only the specified app ( mvt ba checkito nginx styxpres )
    stop-app <app_id>                             Stop only the specified app ( mvt ba checkito nginx styxpres )

----

*[WINDOWS]*:

If you're using Git BASH and the above command is not working you may need to use _sh_ instead of _./_ :

    sh local_env.sh <command> <options>
----

#### Start

    ./local_env.sh start -ba-version <ba-version>

In order to check that everything works you can open the following [stubbed hotel link](https://www.dev-hotels.com/booking/deep_link.html?pos=HCOM_US&locale=en_US&mvariant=1327.0%2C1943.1%2C1544.1%2C1400.1%2C985.1%2C1156.0%2C810.1%2C1881.1%2C316.1%2C1947.1%2C1539.1%2C839.2%2C1306.1%2C1735.1%2C4418.1&arrivalDate=09-12-2018&departureDate=10-12-2018&currency=USD&rooms%5B0%5D.numberOfAdults=2&rooms%5B0%5D.numberOfChildren=0&hotelId=434772&roomTypeCode=200310048&rateCode=201876673&businessModel=MERCHANT&ratePlanConfiguration=REGULAR&hotelContractCardinality=SINGLE)

*Note:* the first time you start the local environment the setup may take a few minutes, since it needs to downloads various docker images.
    
#### Stop

    ./local_env.sh stop

#### Status

    ./local_env.sh status

#### Start a single app

    ./local_env.sh start-app <app> <options>

*Examples*
    
* `./local_env.sh start-app ba -ba-version 123.0.7220`
* `./local_env.sh start-app checkito`

#### Stop a single app

    ./local_env.sh stop-app <app>

*Examples*
    
* `./local_env.sh stop-app ba`
* `./local_env.sh start-app checkito`

### BA testing

You can test the following BA use case:
* BA stable version 
* BA feature-branch
* BA built in local

the only difference between the 3 use cases above is the version of the BA to be provided.

**BA version example**

* BA stable version:
 
`./local_env.sh start -ba-version 120.0.7090`

* BA feature-branch: 

`./local_env.sh start -ba-version 120.0.feature_CHOP_2658_availabilty_price_check_feature_branch.4`   

* BA built in local: 

`./local_env.sh start -ba-version dev.0`

*Note:* in order to build the BA in local you need to use the profile `-Pbuild-local`.

    $ cd <bookingapp_root_folder>
    $ mvn clean install -Pbuild-local

### DUP Feature branch testing

DUP feature branch testing in local can be performed in the same way as in staging.
You just need either to specify the DUP `feature-branch` parameter on the BF deeplink or set the DUP feature-branch cookie

### Logging

All the local environment application logs are appended under the folder `logs`.

    $ cd <local_environment_root_folder>/logs
    $ tail -f <app>.log

### BA DEBUG

The fixed BA debugging port is `1901`
You can change this in the local_environment `<local_environment_root_folder>/docker-compose.yml`, but if need to do it please make it configurable via the startup script.

### Proxying

Local proxy is not supported via the startup script yet.
If you need to enable the local proxy you can modify the following configuration into the local_environment `<local_environment_root_folder>/docker-compose.yml` file. Again PR welcomed.

    # Proxy resources
    # - APP_http.proxyHost=docker.for.mac.localhost
    # - APP_http.proxyPort=8888
    # - APP_https.proxyHost=docker.for.mac.localhost
    # - APP_https.proxyPort=8888
    # - APP_proxyHost=docker.for.mac.localhost
    # - APP_proxyPort=8888

## Troubleshooting common issues

* No CSS/JS - This indicates you have not accepted the `a*-cdn-hotels.com` domain certificates. Please trust like you do on staging or milan.

* Not seeing the header? You're not setting the MVT `4418.1`

* Update the docker spotify plugin version to `0.4.13` if you have got the following error building your local image 

    ```org.apache.http.conn.HttpHostConnectException: Connect to localhost:2375 [localhost/127.0.0.1] failed: Connection refused```

* If you're having login error while downloading nginx

    ```Get https://registry-1.docker.io/v2/library/nginx/manifests/mainline-alpine: unauthorized: incorrect username or password```
    
  you may have messed up your docker login and you just need to logout
  
    ```docker logout```

* If you're having connection refused while building the local BA ```mvn clean install -rf :bookingapp-release -Pbuild-local```, you need to expose the docker daemon without TLS.

    ```[ERROR] Failed to execute goal com.spotify:docker-maven-plugin:0.4.13:build (build-docker-image) on project bookingapp-release: Exception caught: java.util.concurrent.ExecutionException: com.spotify.docker.client.shaded.javax.ws.rs.ProcessingException: org.apache.http.conn.HttpHostConnectException: Connect to localhost:2375 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect```
    
    ![WIN_docker_expose_daemon_without_TLS](assets/WIN_docker_expose_daemon_without_TLS.png)

## FAQ

TBW

## Contributing

Contribution is always the key. If you find any issue or you want to make an improvement, please open a PR and ask your CKO friends to review it.

If you feel something is missing or you want to suggest any improvement, please report it to this [confluence page](https://confluence/pages/viewpage.action?pageId=878693711) 

## Developers Notes

The local env is based on the STYX project local-app-server: http://stash.hcom/projects/STYX/repos/local-app-server/browse

Components involved:
* ngnix: reverse proxy
* styx: html page rendering
* CKO FE apps: BA, BMA, BCA
* checkito: CKO mock servers

Styx DUP plugin only serves the html (rendering the soy files), the assets (JS, CSS) are served by the staging DispatcherApp (DA):
* the DA reads the JS and CSS from File System DUP folders
* there is a DUP folder for each branch
