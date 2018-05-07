package main.kotlin

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

object PropertyRegexFactory {

    /**
     * Makes expected regular expressions fro all properties.
     */
    suspend internal fun createRegex(properties: Collection<out String>, template: String): Map<String, Regex> {

        val deferredMap = hashMapOf<String, Deferred<Regex>>()
        properties.forEach {
            deferredMap.put(it, async { Regex(template.replace("Property", it)) })
        }
       return deferredMap.entries.associate { it.key to it.value.await() }
    }
}