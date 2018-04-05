# configuration-tracer

A tool to find unused configuration files in the BA-family.

Owner: CKO/Pluto, slack: #hcom-cko-pluto-sup

### What this tool does:
  - report all unused properties

### What it does not do:
  - delete reported properties from the given files

### How it works
  - collect property files, xml configurations, JSP and java files
  - walk through all files and check all of the properties usage

### Configuration
Use java system properties to set these properties (= edit the run.sh) script
  - configSource = the root folder of the configuration files (= resources/conf/environment)
  - projectRoot = root folder of the project (= bookingapp)

### Build and run
Edit the config values in run.sh, then:

```
  $ mvn clean install
  $ ./run.sh
```

