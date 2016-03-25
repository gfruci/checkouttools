btr - Backup test result. Used to copy UI test result to an external directory which is hosted via http server

get_confmail.sh - script to get confirmation email from Checkito. It prints the email to the console

trace_log_console - format tracelog for console (colors and indentations)

trace_log_html - format tracelog into html (colors, collapsable, fancy)


module-desc - Python script to parse all module-descriptor in the specified directory and also extract descriptor files from jars.
            - Usage: python ./module-desc.py c:\work\git\bookingapp
	    -        print out all modules all dependecies
	    -
	    - Usage: python ./module-desc.py c:\work\git\bookingapp bookingapp-service-module-descriptor.xml
	    -        print out the specified module dependecy tree
