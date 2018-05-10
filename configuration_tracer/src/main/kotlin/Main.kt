package main.kotlin

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.time.Instant

fun main(args: Array<String>) {

    val start: Long = Instant.now().toEpochMilli()
    val propertyFiles = FileCollector.getAllFiles(Config.confSrc, "properties")
    val allFiles: MutableList<String> = ArrayList<String>()
    allFiles.addAll(propertyFiles)
    allFiles.addAll(FileCollector.getAllFiles(Config.projRoot, "jsp", "jspf", "xml", "java", "ftl"))
    val defaultPropertyFile = propertyFiles.filter { it.endsWith(Config.DEFAULT_PROPERTY_FILE) }[0]

    println("================= Unused properties ================")

    val allProperties = PropertyCollector.getPropertiesOf(defaultPropertyFile)

    runBlocking {
        val propertyRegexMap = PropertyRegexFactory.createRegex(allProperties, Config.REGEX_TEMPLATE)
        val deferredUsedProperties = async{ PropertyUsageService.getUsedProperties(propertyRegexMap, allFiles) }
        val deferredUsedPropertiesCodeSearch = async { PropertyUsageService.getUsedPropertiesByCodeSearch(propertyRegexMap) }

        allProperties.minus(deferredUsedProperties.await()).minus(deferredUsedPropertiesCodeSearch.await()).forEach { println(it) }
    }

    println("Elapsed time: " + Instant.now().toEpochMilli().minus(start))
}

