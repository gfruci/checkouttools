class ConsoleArguments:
    def __init__(self, target_folder, configuration_file, message_keys_file, postfix):
        self._target_folder = target_folder
        self._configuration_file = configuration_file
        self._message_keys_file = message_keys_file
        self._postfix = postfix

    @property
    def target_folder(self):
        return self._target_folder

    @property
    def configuration_file(self):
        return self._configuration_file

    @property
    def message_keys_file(self):
        return self._message_keys_file

    @property
    def postfix(self):
        return self._postfix
