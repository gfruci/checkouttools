# Message Key Postfixer

## Description
This barely-a-program appends in a target folder - and in its sub-folders recursively - 
the given postfix to every message key listed in a file
if the next character following the message key is one of the valid terminal characters.

The program can ignore folders from an `excluded folders` list and work in only files which have 
one of the `included file extensions`.

As it replaces the message keys with their postfixed version in place, it needs to create a temporary file
which has the same name as the original file but ends with the `temporary file extension`.

## History

This project was originally created for the Unhotelling effort on CKO.
The goal was to have a tool which can be used to quickly update lots of message keys with the ".unhotelling" postfix.

## Usage

The program runs on python major version 3, recommended is 3.7 and above.

It does not support python 2.

Convention over configuration is applied here, this is the simplest way to run the program:

`./message_key_postfixer.py c:/hcom/git/bookingapp`

It will work inside the provided path with the default values detailed below.

Reports will be generated in the current working directory.

### Command Line Arguments

This is an example using all the possible arguments:

`./message_key_postfixer.py c:/hcom/git/bookingapp -c config.ini -m message_keys.txt -p .unhotelling`

- "c:/hcom/git/bookingapp" is an example for the target folder where the message keys will should be postfixed - 
this is the only mandatory argument
- `-c` or `--configfile`: path to the configuration file
    - default value: `config.ini`
    - it contains the following configuration values:
        - `ExcludeFolders`: ", " (a comma and a space character) separated list: if any folder 
        under the target folder contains anywhere inside any of these strings then it will be excluded
            - default value: `.git, .idea`
        - `IncludeFilesWithExtensions`: ", " (a comma and a space character) separated list: 
        only check the files which has the extension in this list
            - default value: `.java, .xml, .json, .properties, .yml, .txt`
        - `ValidTerminalCharacters`: ", " (a comma and a space character) separated list: 
        all the valid terminal characters which allows postfixing
            - default value: `", <, |, =, )`
            - note that **whitespace is always a valid terminal character**, 
            so message keys at the end of a line will always be postfixed!
        - `TemporaryFileExtension`: extension of the temporary file, 
        should be an extension not existing under the target folder
            - default value: `.bk`
- `-m` or `--messagekeysfile`: contains all the message keys which should be postfixed
    - default value: `message_keys.txt`
    - the internal format of the file is that every line contains one message key (and the line ending, of course)
- `-p` or `--postfix`: the postfix to be appended to the message keys
    - default value: `.unhotelling`
- `-h` or `--help`: prints the help with usage

### Terminal Characters Explained
If the " (double quotes) character is a valid terminal character, 
the message key is `just.one.key` and the postfix is `.postfix` then the program will replace this line:

`private static final MESSAGE_KEY = "just.one.key";`

to this line:

`private static final MESSAGE_KEY = "just.one.key.postfix";`

but will not modify a line like this - if the . (dot) character is not a valid terminal character:

`private static final MESSAGE_KEY = "just.one.key.which.contains.the.other;"`

This is to prevent appending the postfix inside message keys which contain the original message key.

### Reports

The following reports are created by the program at the end of its run:
- `postfixed_message_keys.txt`: message keys occurrences where they were postfixed
- `not_found_message_keys.txt`: message keys not found in the target folder
- `invalid_terminal_character_occurrences.txt`: message key occurrences where they were not postfixed because of 
an invalid terminal character 
- `multiple_occurrences_in_line.txt`: message key occurrences where it is occurring more than once within a line
- `ignored_folders.txt`: all the folders ignored during the run

These reports are prefixed with the time of their creation in order to prevent overwriting them.
The format of this prefix is: `YYYYMMDD_HHmmDD_`.

An example of an actual report is:

`20190314_140500_not_found_message_keys.txt`

## Support

If you have any question, need help to configure or use the program,
found any bug or would like to comment then please turn to the HCOM Checkout CHOP Team.
