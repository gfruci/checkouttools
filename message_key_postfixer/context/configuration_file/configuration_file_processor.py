import configparser

from context.configuration_file.configuration_values import ConfigurationValues


class ConfigurationFileProcessor:
    def __init__(self):
        self.configuration_parser = configparser.ConfigParser()

    def process(self, configuration_file_path):
        self.configuration_parser.read(configuration_file_path)
        excluded_folders = self.configuration_parser["Main"]["ExcludeFolders"].split(", ")
        included_files_extensions = self.configuration_parser["Main"]["IncludeFilesWithExtensions"].split(", ")
        valid_terminal_characters = self.configuration_parser["Main"]["ValidTerminalCharacters"].split(", ")
        temporary_file_extension = self.configuration_parser["Main"]["TemporaryFileExtension"]
        return ConfigurationValues(excluded_folders, included_files_extensions, valid_terminal_characters,
                                   temporary_file_extension)
