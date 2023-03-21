package com.expedia.ccat5563.mutation

import com.expedia.ccat5563.client.LobotApiClient
import com.expedia.ccat5563.documentation.ConfluenceHtmlGenerator
import com.expedia.ccat5563.domain.Rerouting

private const val SHARED_CONDITION_ENTITY_TYPE = "sharedConditions"

class SharedConditionModifier(
    private val lobotApiClient: LobotApiClient,
    private val confluenceHtmlGenerator: ConfluenceHtmlGenerator
) {
    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Shared-Conditions#create-shared-condition
     * returns: HTML link to the new shared condition
     */
    fun create(authToken: String, rerouting: Rerouting, host: String, environment: String): String {
        val sharedConditionIdentifier = "ccat5563-h2b-pb-${rerouting.posHumanName}-traffic-$environment"
        val requestBody = """
{
  "query": "mutation addEntity(${'$'}sharedCondition: SharedConditionInput!) { addSharedCondition(sharedCondition: ${'$'}sharedCondition) { id identifier description version conditions audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "sharedCondition": {
      "identifier": "$sharedConditionIdentifier",
      "description": "Evaluates whether the HCOM Classic post-booking traffic (to be rerouted to HoB Trip Overview page) comes from ${rerouting.oldPos} pos with ${rerouting.oldLocale} locale in $environment. More details: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "conditions": {
        "and": [
          {
            "header": {
              "name": "Host",
              "match": "EQUAL_IGNORE_CASE",
              "type": "request",
              "value": "$host"
            }
          },
          {
            "query": {
              "name": "pos",
              "match": "EQUAL_IGNORE_CASE",
              "value": "${rerouting.oldPos}"
            }
          },
          {
            "query": {
              "name": "locale",
              "match": "EQUAL_IGNORE_CASE",
              "value": "${rerouting.oldLocale}"
            }
          }
        ]
      }
    }
  }
}            
        """.trimIndent()
        return sendCreationRequestAndProcessResponse(authToken, requestBody, sharedConditionIdentifier)
    }

    private fun sendCreationRequestAndProcessResponse(authToken: String, requestBody: String, identifier: String): String {
        val response = lobotApiClient.httpPostResponseBody(authToken, requestBody)
        println(response)
        return confluenceHtmlGenerator.generateHtmlLink(SHARED_CONDITION_ENTITY_TYPE, response, identifier)
    }
}
