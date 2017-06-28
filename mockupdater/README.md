# Internationalisation mock updater

> The localization of BA is mocked out in our tests. These mocked messages are coming from Checkito. 
The purpose of this utility is to make updating these less painful. :) 

## Dependency
* Checkito

## Installation
```bash
$ git clone ssh://git@stash.hcom:7999/cop/checkouttools.git
$ cd mockupdater
$ mvn clean install 
```

## Usage
1. Update the `i18n_messages.json` in the Checkout repository.
2. Execute the jar. Note: the original json file will be overwritten.
```bash
$ java -jar target/mockupdater-{project.version}-jar-with-dependencies.jar "{path to i18n_messages.json in Checkout}"
```
3. Check the diff and commit your changes in the Checkout repository.
