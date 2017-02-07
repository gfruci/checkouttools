## Style Guide
1. Please try to follow Google Shell Coding Style guide if possible
    https://google.github.io/styleguide/shell.xml

2. Please create help for scripts with -h option

3. Please update this readme with a short description about your script and an example if needed

###acceptance_test_jvm_args.sh
Script to collect jvm args for Acceptance test execution from IDE.

###btr
Backup test result. Used to copy UI test result to an external directory which is hosted via http server

###get_confmail.sh
script to get confirmation email from Checkito. It prints the email to the console

###trace_log_console
format tracelog for console (colors and indentations)

###trace_log_html
format tracelog into html (colors, collapsable, fancy)

###get_all_repos.py
Collects all repository from Codesearch where query applied
1. Usage: ```./get_all_repos.py "front-end-app-base" "Dockerfile"```
2. Usage: ```./get_all_repos.py "<query>" "<files>"```


###module-desc
Python script to parse all module-descriptor in the specified directory and also extract descriptor files from jars.

1. Usage: ```python ./module-desc.py c:\work\git\bookingapp```

    print out all modules all dependecies

2. Usage: ```python ./module-desc.py c:\work\git\bookingapp bookingapp-service-module-descriptor.xml```

    print out the specified module dependecy tree
