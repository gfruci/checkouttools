package main.kotlin

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.io.File

object PropertyUsageService {

    /**
     * Returns all used properties, which are referenced in the any files.
     */
    fun getUsedProperties(properties: List<out String>, filesToCheck: Collection<String>, template: String): Set<out String> =

        runBlocking {
            filesToCheck.map {
                async {
                    val content = File(it).readText()
                    properties.filter { Regex(template.replace("Property", it)).containsMatchIn(content) }
                }
            }.map { it.await() }.flatten().toSet()
        }

}