import os


class PostfixerFacade:
    def __init__(self, excluded_folder_checker, postfixer):
        self.excluded_folder_checker = excluded_folder_checker
        self.postfixer = postfixer

    def postfix(self, context):
        for sub_folder, folders, files in os.walk(context.target_folder):
            if self.excluded_folder_checker.is_excluded(context.excluded_folders, sub_folder):
                context.reports.ignored_folders.append(sub_folder)
                continue
            for file in files:
                if file.endswith(context.included_files_extensions):
                    self.postfixer.postfix(os.path.join(sub_folder, file), context)
