import datetime


class ReportGeneratorFacade:
    def __init__(self, postfixed_message_keys_report_generator, not_found_message_keys_report_generator,
                 invalid_terminal_character_occurrences_report_generator,
                 multiple_occurrences_in_line_report_generator,
                 ignored_folders_report_generator):
        self.postfixed_message_keys_report_generator = postfixed_message_keys_report_generator
        self.not_found_message_keys_report_generator = not_found_message_keys_report_generator
        self.invalid_terminal_character_occurrences_report_generator = \
            invalid_terminal_character_occurrences_report_generator
        self.multiple_occurrences_in_line_report_generator = multiple_occurrences_in_line_report_generator
        self.ignored_folders_report_generator = ignored_folders_report_generator

    def generate_reports(self, context):
        datetime_stamp_prefix = datetime.datetime.now().strftime("%Y%m%d_%H%M%S_")
        self.postfixed_message_keys_report_generator.generate_report(context, datetime_stamp_prefix)
        self.not_found_message_keys_report_generator.generate_report(context, datetime_stamp_prefix)
        self.invalid_terminal_character_occurrences_report_generator.generate_report(context, datetime_stamp_prefix)
        self.multiple_occurrences_in_line_report_generator.generate_report(context, datetime_stamp_prefix)
        self.ignored_folders_report_generator.generate_report(context, datetime_stamp_prefix)
