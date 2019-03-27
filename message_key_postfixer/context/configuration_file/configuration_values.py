class ConfigurationValues:
    def __init__(self, excluded_folders, included_files_extensions, valid_terminal_characters,
                 temporary_file_extension):
        self._excluded_folders = excluded_folders
        self._included_files_extensions = included_files_extensions
        self._valid_terminal_characters = valid_terminal_characters
        self._temporary_file_extension = temporary_file_extension

    @property
    def excluded_folders(self):
        return self._excluded_folders

    @property
    def included_files_extensions(self):
        return self._included_files_extensions

    @property
    def valid_terminal_characters(self):
        return self._valid_terminal_characters

    @property
    def temporary_file_extension(self):
        return self._temporary_file_extension
