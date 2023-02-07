package com.expedia.ccat5563.documentation

private const val DELIMITER_COLON = ':'
private const val DELIMITER_COMMA = ','
private const val IGNORE_CASE = true
private const val SUBSTRING_LIMIT = 100
private const val ID_INDEX = 3
private const val COLUMN_START = "<td>"
private const val COLUMN_END = "</td>"

class ConfluenceHtmlGenerator {
    private val htmlRows: MutableMap<String, String> = HashMap()

    fun generateHtmlLink(lobotEntityType: String, creationResponse: String, identifier: String): String {
        val id = extractId(creationResponse)
        return """
        <p>
          <a href="https://lobot.prod.expedia.com/highlander/$lobotEntityType/$id">$identifier</a>
        </p>
        """.trimIndent()
    }

    private fun extractId(creationResponse: String): String {
        val subStrings = creationResponse.split(DELIMITER_COLON, DELIMITER_COMMA, ignoreCase = IGNORE_CASE, limit = SUBSTRING_LIMIT)
        return subStrings[ID_INDEX]
    }

    fun generateHtmlRowAndSave(posHumanName: String, redirectLinks: List<String>, sharedConditionLinks: List<String>, routingRuleLinks: List<String>) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(COLUMN_START)
        redirectLinks.forEach{ stringBuilder.append(it) }
        stringBuilder.append(COLUMN_END)
        stringBuilder.append(COLUMN_START)
        sharedConditionLinks.forEach { stringBuilder.append(it) }
        stringBuilder.append(COLUMN_END)
        stringBuilder.append(COLUMN_START)
        routingRuleLinks.forEach { stringBuilder.append(it) }
        stringBuilder.append(COLUMN_END)
        htmlRows[posHumanName] = stringBuilder.toString()
    }

    fun printHtmlRows() {
        htmlRows.forEach{
            println("${it.key}:")
            println(it.value)
            println()
        }
    }
}
