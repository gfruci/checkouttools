class Reports:
    def __init__(self, postfixed_message_keys_report, invalid_terminal_character_occurrences,
                 multiple_occurrences_in_line, ignored_folders):
        self._postfixed_message_keys_report = postfixed_message_keys_report
        self._invalid_terminal_character_occurrences = invalid_terminal_character_occurrences
        self._multiple_occurrences_in_line = multiple_occurrences_in_line
        self._ignored_folders = ignored_folders

    @property
    def postfixed_message_keys_report(self):
        return self._postfixed_message_keys_report

    @property
    def invalid_terminal_character_occurrences(self):
        return self._invalid_terminal_character_occurrences

    @property
    def multiple_occurrences_in_line(self):
        return self._multiple_occurrences_in_line

    @property
    def ignored_folders(self):
        return self._ignored_folders
