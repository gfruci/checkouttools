package com.expedia.ccat5563.mutation.routingrule

import com.expedia.ccat5563.client.LobotApiClient

private const val DELIMITER_COLON = ':'
private const val CLOSING_CURLY_BRACE = '}'
private const val IGNORE_CASE = true
private const val SUBSTRING_LIMIT = 10
private const val FOURTH_INDEX = 3

class RoutingRuleIdentifierToIdConverter(
    private val lobotApiClient: LobotApiClient
) {
    fun convert(authToken: String, ruleIdentifier: String): String {
        val response = sendRequest(ruleIdentifier, authToken)
        val subStrings = response.split(DELIMITER_COLON, CLOSING_CURLY_BRACE, ignoreCase = IGNORE_CASE, limit = SUBSTRING_LIMIT)
        return subStrings[FOURTH_INDEX]
    }

    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/blob/920930904a3c1013fe4dd58bace6e76ee87db1cf/src/main/java/com/expedia/www/lobot/highlander/web/query/routing/EndpointRuleQuery.java#L97
     */
    private fun sendRequest(ruleIdentifier: String, authToken: String): String {
        val requestBody = """
            {
              "query": "query routingRuleIdentifierToId(${'$'}identifier: String!) { endpointRuleByIdentifier(identifier: ${'$'}identifier) { id } }",
              "variables": {
                "identifier": "$ruleIdentifier"          
              }
            }"
            """.trimIndent()
        return lobotApiClient.httpPostResponseBody(authToken, requestBody)
    }
}
