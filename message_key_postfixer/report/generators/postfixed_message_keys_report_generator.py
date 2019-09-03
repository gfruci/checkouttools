class PostfixedMessageKeysReportGenerator:
    def generate_report(self, context, prefix):
        with open(prefix + "postfixed_message_keys.txt", "w", encoding="utf-8") as file_handle:
            for message_key, files in context.reports.postfixed_message_keys_report.items():
                file_handle.write(f"{message_key}\n")
                for file in files:
                    file_handle.write(f"\t{file}\n")
