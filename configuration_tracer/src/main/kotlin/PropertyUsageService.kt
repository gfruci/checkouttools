package main.kotlin

import khttp.get
import khttp.responses.Response
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.io.File

object PropertyUsageService {

    /**
     * Returns all used properties, which are referenced in any files.
     */
    suspend fun getUsedProperties(propertiesWithRegex: Map<String, Regex>, filesToCheck: Collection<String>): Set<String> =
            filesToCheck.map { getUsedPropertiesInFile(it, propertiesWithRegex) }.map { it.await() }.flatten().toSet()

    private fun getUsedPropertiesInFile(it: String, propertiesWithRegex: Map<String, Regex>): Deferred<List<String>> {
        return async {
            val content = File(it).readText()
            propertiesWithRegex.keys.filter {
                val regex = propertiesWithRegex[it]
                if (regex != null) regex.containsMatchIn(content) else true
            }
        }
    }

    /**
     * Returns all used properties, which are referenced by code search.
     */
    suspend fun getUsedPropertiesByCodeSearch(propertiesWithRegex: Map<String, Regex>): Set<out String> {

        val deferredResponsesMap = hashMapOf<String, Deferred<Response>>()
        propertiesWithRegex.entries.forEach {
            deferredResponsesMap.put(it.key, async {
                get(
                        url = "http://codesearch.hcom/api/v1/search",
                        params = mapOf("repos" to "*", "q" to "${it.value}", "files" to "(java|xml|jsp|jspf|ftl)", "ctx" to "0"),
                        headers = mapOf("Accept" to "application/json;charset=UTF-8", "Content-Type" to "application/json;charset=UTF-8", "Authorization" to "Basic ${Config.credential}")
                )
            })
        }
        return deferredResponsesMap.entries.filter { isPropertyUsed(it) }.map { it.key }.toSet()
    }

    private suspend fun isPropertyUsed(entry: MutableMap.MutableEntry<String, Deferred<Response>>): Boolean =
            entry.value.await().statusCode != 200 || entry.value.await().jsonObject.get("Results").toString() != "{}"
}