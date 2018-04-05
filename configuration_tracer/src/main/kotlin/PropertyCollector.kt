package main.kotlin

import java.io.File

object PropertyCollector {

    private val PROPERTY_KEY_REGEX = Regex("[A-Z_]+=")

    /**
     * Returns all properties from the given property file.
     */
    fun getPropertiesOf(propertyFile: String) : List<out String> = File(propertyFile).readLines()
            .map {
                PROPERTY_KEY_REGEX.findAll(it)
                        .map { it.groupValues[0].replace("=", "") }
                        .toList()
            }
            .flatten()

}