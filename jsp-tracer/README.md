# jsp-tracer

A tool to find unused JSP files in the BA-family.

Owner: CKO/Pluto, slack: #hcom-cko-pluto-sup

### What this tool does:
  - report JSP file declarations in XML configs that point to non-existing files
  - report/delete existing JSP files that are unused (non-reachable from XML-s)

### What it does not do:
  - find usages outside of the repository, eg. a JSP referenced by a common
    hcommodules controller, like HeaderFooterController.
  - find relative JSP references. 99% of JSP inclusions are done by their full
    path, starting with "/WEB-INF/..."

### How it works
  - parse XML configurations, find JSP declarations with regex
  - walk through all JSP files, recursively parse includes in them
  - subtract the walked JSP filed from all of them = unused files

### Configuration
Use java system properties to set these properties (= edit the run.sh) script
  - jspPathPrefix = the root folder of the JSP files (= webapp folder)
  - webSrc = src folder of the web module (= bookingapp/bookingapp-web/src)

### Build and run
Edit the config values in run.sh, then:

```
  $ mvn clean install
  $ ./run.sh
```

Use the **-D** or **--delete** flags to delete the unused JSP files.