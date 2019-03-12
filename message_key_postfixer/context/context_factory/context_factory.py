from context.context import Context
from context.reports import Reports


class ContextFactory:
    def create(self, console_arguments, configuration_values, message_keys):
        reports = Reports(postfixed_message_keys_report={}, invalid_terminal_character_occurrences={},
                          multiple_occurrences_in_line={}, ignored_folders=[])
        return Context(console_arguments.target_folder, console_arguments.postfix,
                       tuple(configuration_values.excluded_folders),
                       tuple(configuration_values.included_files_extensions),
                       tuple(configuration_values.valid_terminal_characters),
                       configuration_values.temporary_file_extension, message_keys, reports)
