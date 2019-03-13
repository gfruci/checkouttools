class MessageKeysFileProcessor:
    def process(self, message_keys_file_path):
        message_keys = []
        with open(message_keys_file_path, "r", encoding="utf-8") as file_handle:
            for line in file_handle:
                if line == "" or line.startswith("#"):
                    continue
                message_keys.append(line.strip())
        return message_keys
