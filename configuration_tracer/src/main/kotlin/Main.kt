package main.kotlin

import java.time.Instant

fun main(args: Array<String>) {

    val start: Long = Instant.now().toEpochMilli()
    val propertyFiles = FileCollector.getAllFiles(Config.confSrc, "properties")
    val allFiles: MutableList<String> = ArrayList<String>()
    allFiles.addAll(propertyFiles)
    allFiles.addAll(FileCollector.getAllFiles(Config.projRoot, "jsp", "jspf", "xml", "java"))
    val defaultPropertyFile = propertyFiles.filter { it.endsWith(Config.DEFAULT_PROPERTY_FILE) }[0]

    println("================= Unused properties ================")

    val allProperties = PropertyCollector.getPropertiesOf(defaultPropertyFile)
    val usedProperties = PropertyUsageService.getUsedProperties(allProperties, allFiles, Config.REGEX_TEMPLATE)

    allProperties.minus(usedProperties).forEach {
        println(it)
    }

    println("Elapsed time: " + Instant.now().toEpochMilli().minus(start))
}

