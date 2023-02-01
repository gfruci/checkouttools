package com.expedia.ccat5563.mutation

import com.expedia.ccat5563.client.LobotApiClient
import com.expedia.ccat5563.domain.Rerouting

class SharedConditionModifier(
    private val lobotApiClient: LobotApiClient
) {
    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Shared-Conditions#create-shared-condition
     */
    fun create(authToken: String, rerouting: Rerouting, oldLabHost: String) {
        val requestBody = """
{
  "query": "mutation addEntity(${'$'}sharedCondition: SharedConditionInput!) { addSharedCondition(sharedCondition: ${'$'}sharedCondition) { id identifier description version conditions audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "sharedCondition": {
      "identifier": "ccat5563-h2b-pb-${rerouting.posHumanName}-traffic",
      "description": "Evaluates whether the HCOM Classic post-booking traffic (to be rerouted to HoB Trip Overview page) comes from ${rerouting.oldPos} pos with ${rerouting.oldLocale} locale. More details: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "conditions": {
        "and": [
          {
            "or": [
              {
                "header": {
                  "name": "Host",
                  "match": "EQUAL_IGNORE_CASE",
                  "type": "request",
                  "value": "${rerouting.oldProdHost}"
                }
              },
              {
                "header": {
                  "name": "Host",
                  "match": "EQUAL_IGNORE_CASE",
                  "type": "request",
                  "value": "$oldLabHost"
                }
              }
            ]
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
        println(lobotApiClient.httpPostResponseBody(authToken, requestBody))
    }
}
