package com.expedia.ccat5563.mutation.routingrule

import com.expedia.ccat5563.client.LobotApiClient
import com.expedia.ccat5563.domain.RuleUpdateParams

private const val DELIMITER_COLON = ':'
private const val DELIMITER_CLOSING_CURLY_BRACE = '}'
private const val DELIMITER_COMMA = ','
private const val IGNORE_CASE = true
private const val SUBSTRING_LIMIT = 100
private const val ID_INDEX = 3
private const val VERSION_INDEX = 5

class RuleUpdateParamsRetriever(
    private val lobotApiClient: LobotApiClient
) {
    fun retrieve(authToken: String, ruleIdentifier: String): RuleUpdateParams {
        val response = sendRuleUpdateParamsRequest(ruleIdentifier, authToken)
        println(response)
        val subStrings = response.split(DELIMITER_COLON, DELIMITER_CLOSING_CURLY_BRACE, DELIMITER_COMMA, ignoreCase = IGNORE_CASE, limit = SUBSTRING_LIMIT)
        return RuleUpdateParams(subStrings[ID_INDEX], subStrings[VERSION_INDEX])
    }

    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/blob/920930904a3c1013fe4dd58bace6e76ee87db1cf/src/main/java/com/expedia/www/lobot/highlander/web/query/routing/EndpointRuleQuery.java#L97
     */
    private fun sendRuleUpdateParamsRequest(ruleIdentifier: String, authToken: String): String {
        val requestBody = """
            {
              "query": "query getUpdateParams(${'$'}identifier: String!) { endpointRuleByIdentifier(identifier: ${'$'}identifier) { id version } }",
              "variables": {
                "identifier": "$ruleIdentifier"          
              }
            }"
            """.trimIndent()
        return lobotApiClient.httpPostResponseBody(authToken, requestBody)
    }

    fun getIdForIdentifier(authToken: String, ruleIdentifier: String): String {
        val response = sendIdRequest(ruleIdentifier, authToken)
        println(response)
        val subStrings = response.split(DELIMITER_COLON, DELIMITER_CLOSING_CURLY_BRACE, DELIMITER_COMMA, ignoreCase = IGNORE_CASE, limit = SUBSTRING_LIMIT)
        return subStrings[ID_INDEX]
    }

    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/blob/920930904a3c1013fe4dd58bace6e76ee87db1cf/src/main/java/com/expedia/www/lobot/highlander/web/query/routing/EndpointRuleQuery.java#L97
     */
    private fun sendIdRequest(ruleIdentifier: String, authToken: String): String {
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
