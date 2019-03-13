class Context:
    def __init__(self, target_folder, postfix, excluded_folders, included_files_extensions, valid_terminal_characters,
                 temporary_file_extension, message_keys, reports):
        self._target_folder = target_folder
        self._postfix = postfix
        self._excluded_folders = excluded_folders
        self._included_files_extensions = included_files_extensions
        self._valid_terminal_characters = valid_terminal_characters
        self._temporary_file_extension = temporary_file_extension
        self._message_keys = message_keys
        self._reports = reports

    @property
    def target_folder(self):
        return self._target_folder

    @property
    def postfix(self):
        return self._postfix

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

    @property
    def message_keys(self):
        return self._message_keys

    @property
    def reports(self):
        return self._reports
