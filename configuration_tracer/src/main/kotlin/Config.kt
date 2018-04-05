package main.kotlin

import java.io.File
import java.util.regex.Pattern

internal object Config {

    private const val CONFIG_SOURCE : String = "configSource"
    private const val PROJECT_ROOT : String = "projectRoot"
    internal const val REGEX_TEMPLATE : String = "(\\\"Property\\\"|is\\(\\\"Property\\\"\\)|is\\(Property\\)|\\\$\\{Property\\})";
    internal const val DEFAULT_PROPERTY_FILE = "env_rules_default.properties"

    internal var confSrc: String
    internal var projRoot : String

    init {
        confSrc = System.getProperty(CONFIG_SOURCE)
        projRoot = System.getProperty(PROJECT_ROOT)

        if (!File(confSrc).isDirectory || !File(projRoot).isDirectory) {
            throw IllegalStateException("Not a valid directory: ${confSrc} or ${projRoot}")
        }
    }
}