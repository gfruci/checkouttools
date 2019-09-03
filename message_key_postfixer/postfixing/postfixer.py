import os


class Postfixer:
    def postfix(self, file_path, context):
        with open(file_path, mode="r", encoding="utf-8", newline="") as read_file_handle:
            with open(file_path + context.temporary_file_extension, mode="w", encoding="utf-8",
                      newline="") as write_file_handle:
                for line in read_file_handle:
                    for message_key in context.message_keys:
                        if message_key in line:
                            line = self._postfix_message_key(context, file_path, line, message_key)
                    write_file_handle.write(line)
        self._replace_files(context, file_path)

    def _postfix_message_key(self, context, file_path, line, message_key):
        parts = line.split(message_key)
        if len(parts) == 2:
            part_after_message_key = parts[1]
            if part_after_message_key.startswith(context.valid_terminal_characters) or \
                    part_after_message_key[0].isspace():
                line = line.replace(message_key, message_key + context.postfix)
                self._report_postfixed_message_key(context, message_key, file_path)
            else:
                self._report_invalid_terminal_character_occurrences(context, message_key, file_path, line,
                                                                    part_after_message_key[0])
        else:
            self._report_multiple_occurrences_in_line(context, message_key, file_path, line)
        return line

    def _replace_files(self, context, file_path):
        os.remove(file_path)
        os.rename(file_path + context.temporary_file_extension, file_path)

    def _report_postfixed_message_key(self, context, message_key, file_path):
        if message_key not in context.reports.postfixed_message_keys_report:
            context.reports.postfixed_message_keys_report[message_key] = []
        if file_path not in context.reports.postfixed_message_keys_report[message_key]:
            context.reports.postfixed_message_keys_report[message_key].append(file_path)

    def _report_invalid_terminal_character_occurrences(self, context, message_key, file_path, line,
                                                       invalid_terminal_character):
        if message_key not in context.reports.invalid_terminal_character_occurrences.keys():
            context.reports.invalid_terminal_character_occurrences[message_key] = {}
        if file_path not in context.reports.invalid_terminal_character_occurrences[message_key].keys():
            context.reports.invalid_terminal_character_occurrences[message_key][file_path] = []
        context.reports.invalid_terminal_character_occurrences[message_key][file_path].append(
            {"line": line, "invalid_terminal_character": invalid_terminal_character})

    def _report_multiple_occurrences_in_line(self, context, message_key, file_path, line):
        if message_key not in context.reports.multiple_occurrences_in_line.keys():
            context.reports.multiple_occurrences_in_line[message_key] = {}
        if file_path not in context.reports.multiple_occurrences_in_line[message_key].keys():
            context.reports.multiple_occurrences_in_line[message_key][file_path] = []
        context.reports.multiple_occurrences_in_line[message_key][file_path].append(line)
