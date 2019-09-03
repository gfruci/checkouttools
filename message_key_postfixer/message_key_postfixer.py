#!/usr/bin/env python
# -*- coding: utf-8 -*-
from context.configuration_file.configuration_file_processor import ConfigurationFileProcessor
from context.console_arguments.console_arguments_processor import ConsoleArgumentProcessor
from context.context_builder_facade import ContextBuilderFacade
from context.context_factory.context_factory import ContextFactory
from context.message_keys.message_keys_file_processor import MessageKeysFileProcessor
from postfixing.excluded_folder_checker import ExcludedFolderChecker
from postfixing.postfixer import Postfixer
from postfixing.postfixer_facade import PostfixerFacade
from report.generators.ignored_folders_report_generator import IgnoredFoldersReportGenerator
from report.generators.invalid_terminal_character_occurrences_report_generator import \
    InvalidTerminalCharacterOccurrencesReportGenerator
from report.generators.multiple_occurrences_in_line_report_generator import MultipleOccurrencesInLineReportGenerator
from report.generators.not_found_message_keys_report_generator import NotFoundMessageKeysReportGenerator
from report.generators.postfixed_message_keys_report_generator import PostfixedMessageKeysReportGenerator
from report.report_generator_service import ReportGeneratorFacade


def initialize_context_builder_facade():
    console_arguments_processor = ConsoleArgumentProcessor()
    configuration_file_processor = ConfigurationFileProcessor()
    message_keys_file_processor = MessageKeysFileProcessor()
    context_factory = ContextFactory()
    return ContextBuilderFacade(console_arguments_processor, configuration_file_processor, message_keys_file_processor,
                                context_factory)


def initialize_postfixer_facade():
    excluded_folder_checker = ExcludedFolderChecker()
    postfixer = Postfixer()
    return PostfixerFacade(excluded_folder_checker, postfixer)


def initialize_report_generator_facade():
    postfixed_message_keys_report_generator = PostfixedMessageKeysReportGenerator()
    not_found_message_keys_report_generator = NotFoundMessageKeysReportGenerator()
    invalid_terminal_character_occurrences_report_generator = InvalidTerminalCharacterOccurrencesReportGenerator()
    multiple_occurrences_in_line_report_generator = MultipleOccurrencesInLineReportGenerator()
    ignored_folders_report_generator = IgnoredFoldersReportGenerator()
    return ReportGeneratorFacade(postfixed_message_keys_report_generator, not_found_message_keys_report_generator,
                                 invalid_terminal_character_occurrences_report_generator,
                                 multiple_occurrences_in_line_report_generator, ignored_folders_report_generator)


if __name__ == '__main__':
    context_builder_facade = initialize_context_builder_facade()
    postfixer_facade = initialize_postfixer_facade()
    report_generator_facade = initialize_report_generator_facade()

    context = context_builder_facade.build_context()
    postfixer_facade.postfix(context)
    report_generator_facade.generate_reports(context)
