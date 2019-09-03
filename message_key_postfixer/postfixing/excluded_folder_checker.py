class ExcludedFolderChecker:
    def is_excluded(self, excluded_folders, folder):
        normalized_folder = folder.replace("\\", "/")
        return any(directory_name in normalized_folder for directory_name in excluded_folders)
