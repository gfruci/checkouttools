class IgnoredFoldersReportGenerator:
    def generate_report(self, context, prefix):
        with open(prefix + "ignored_folders.txt", "w", encoding="utf-8") as file_handle:
            for folder in context.reports.ignored_folders:
                file_handle.write(f"{folder}\n")
