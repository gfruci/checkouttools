class NotFoundMessageKeysReportGenerator:
    def generate_report(self, context, prefix):
        with open(prefix + "not_found_message_keys.txt", "w", encoding="utf-8") as file_handle:
            for message_key in context.message_keys:
                if message_key not in context.reports.postfixed_message_keys_report.keys():
                    file_handle.write(f"{message_key}\n")
