class ContextBuilderFacade:
    def __init__(self, console_arguments_processor, configuration_file_processor, message_keys_file_processor,
                 context_factory):
        self.console_arguments_processor = console_arguments_processor
        self.configuration_file_processor = configuration_file_processor
        self.message_keys_file_processor = message_keys_file_processor
        self.context_factory = context_factory

    def build_context(self):
        console_arguments = self.console_arguments_processor.process()
        configuration_values = self.configuration_file_processor.process(console_arguments.configuration_file)
        message_keys = self.message_keys_file_processor.process(console_arguments.message_keys_file)
        self._display_run_configuration(console_arguments, configuration_values)
        return self.context_factory.create(console_arguments, configuration_values, message_keys)

    def _display_run_configuration(self, console_arguments, configuration_values):
        print()
        print("Postfixer will run with the following configuration:")
        print(f"target folder: {console_arguments.target_folder}")
        print(f"configuration file: {console_arguments.configuration_file}")
        print(f"message keys file: {console_arguments.message_keys_file}")
        print(f"postfix: {console_arguments.postfix}")
        print()
        print("These are the values read from the configuration file:")
        print(f"excluded folders: {', '.join(configuration_values.excluded_folders)}")
        print(f"included files extensions: {', '.join(configuration_values.included_files_extensions)}")
        print(f"valid terminal characters: {', '.join(configuration_values.valid_terminal_characters)}")
        print(f"temporary file extension: {configuration_values.temporary_file_extension}")
        print()
