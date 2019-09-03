import argparse

from context.console_arguments.console_arguments import ConsoleArguments


class ConsoleArgumentProcessor:
    def __init__(self):
        self.parser = argparse.ArgumentParser()
        self.parser.add_argument("target_folder",
                                 help="the folder where the files are which contains the message keys to be replaced")
        self.parser.add_argument("-c", "--configfile", default="config.ini",
                                 help="configuration file used to customize the replacer's behavior, default if omitted: %(default)s")
        self.parser.add_argument("-m", "--messagekeysfile", default="message_keys.txt",
                                 help="text file containing the list of message keys to be replaced, default if omitted: %(default)s")
        self.parser.add_argument("-p", "--postfix", default=".unhotelling",
                                 help="the postfix appended at the end of every message key from the message keys file, default if omitted: %(default)s")

    def process(self):
        arguments = self.parser.parse_args()
        return ConsoleArguments(arguments.target_folder, arguments.configfile, arguments.messagekeysfile,
                                arguments.postfix)
