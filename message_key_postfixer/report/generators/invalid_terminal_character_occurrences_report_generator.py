class InvalidTerminalCharacterOccurrencesReportGenerator:
    def generate_report(self, context, prefix):
        with open(prefix + "invalid_terminal_character_occurrences.txt", "w", encoding="utf-8") as file_handle:
            for message_key, files in context.reports.invalid_terminal_character_occurrences.items():
                file_handle.write(f"{message_key}\n")
                for file in files.keys():
                    file_handle.write(f"\t{file}\n")
                    for item in files[file]:
                        file_handle.write(f"\t\t{item['invalid_terminal_character']} in line: {item['line']}")
