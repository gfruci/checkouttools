package com.expedia.ccat5563

private const val DELIMITER_COLON = ':'
private const val CLOSING_CURLY_BRACE = '}'
private const val IGNORE_CASE = true
private const val SUBSTRING_LIMIT = 10
private const val FOURTH_INDEX = 3

class RoutingRuleIdentifierToIdConverter {
    fun convert(url: String, authToken: String, ruleIdentifier: String): String {
        val response = sendRequest(ruleIdentifier, url, authToken)
        val subStrings = response.split(DELIMITER_COLON, CLOSING_CURLY_BRACE, ignoreCase = IGNORE_CASE, limit = SUBSTRING_LIMIT)
        return subStrings[FOURTH_INDEX]
    }

    private fun sendRequest(ruleIdentifier: String, url: String, authToken: String): String {
        val requestBody = """
            {
              "query": "query routingRuleIdentifierToId(${'$'}identifier: String!) { endpointRuleByIdentifier(identifier: ${'$'}identifier) { id } }",
              "variables": {
                "identifier": "$ruleIdentifier"          
              }
            }"
            """.trimIndent()
        return httpPostResponseBody(url, authToken, requestBody)
    }
}
