class MultipleOccurrencesInLineReportGenerator:
    def generate_report(self, context, prefix):
        with open(prefix + "multiple_occurrences_in_line.txt", "w", encoding="utf-8") as file_handle:
            for message_key, files in context.reports.multiple_occurrences_in_line.items():
                file_handle.write(f"{message_key}\n")
                for file in files.keys():
                    file_handle.write(f"\t{file}\n")
                    for line in files[file]:
                        file_handle.write(f"\t\t{line}")
