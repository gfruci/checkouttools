package main.kotlin

import java.io.File

object FileCollector {

    private val EXCLUDE_SRC_PATH_REGEX: Regex = Regex("(\\\\target\\\\)|(\\\\test-output\\\\)")

    /**
     * Collects all files which are matching with the given extensions and not excluded by the regex
     */
    fun getAllFiles(path : String, vararg extensions: String): List<String> {

        return File(path).walk()
                .filter { it.isFile && extensions.contains(it.extension) && !it.isHidden && !EXCLUDE_SRC_PATH_REGEX.containsMatchIn(it.canonicalPath) }
                .map { it.canonicalPath }
                .toList()
    }
}