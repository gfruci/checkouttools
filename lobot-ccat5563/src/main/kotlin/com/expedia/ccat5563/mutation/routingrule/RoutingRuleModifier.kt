package com.expedia.ccat5563.mutation.routingrule

import com.expedia.ccat5563.capitalized
import com.expedia.ccat5563.client.LobotApiClient
import com.expedia.ccat5563.documentation.ConfluenceHtmlGenerator

private const val RULE_ENTITY_TYPE = "rules"

class RoutingRuleModifier(
    private val lobotApiClient: LobotApiClient,
    private val ruleUpdateParamsRetriever: RuleUpdateParamsRetriever,
    private val confluenceHtmlGenerator: ConfluenceHtmlGenerator
) {
    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Routing-Rules#create-endpoint-rule
     * returns: HTML link to the new rule
     */
    fun createWithEncryptedId(authToken: String, posHumanName: String, environment: String, endpoint: String): String {
        val ruleIdentifier = "ccat5563-h2b-$endpoint-to-trip-overview-with-encrypted-id-$posHumanName-$environment"
        val requestBody = """
{
  "query": "mutation addEntity(${'$'}endpointRule: EndpointRuleInput!) { addEndpointRule(endpointRule: ${'$'}endpointRule) { id endpoint { id identifier description } identifier description site { siteIdentifiers siteHostnames } conditions experiments { experimentId shouldLog } result { application { id identifier description } weight metadata { pageName } interceptErrors nerfMode actions } version audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "endpointRule": {
      "identifier": "$ruleIdentifier",
      "description": "Reroutes HCOM Classic $endpoint (with encrypted id) traffic to HoB Trip Overview when PoSa is ${posHumanName.capitalized()} and environment is $environment. More details: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "tenants": [
        "hcom"
      ],
      "site": {
        "siteIdentifiers": [],
        "siteHostnames": [],
        "brand": "BEX"
      },
      "conditions": {
        "and": [
          {
            "sharedCondition": "h2b-hcpb-to-trip-overview-header-with-reroute-value"
          },
          {
            "sharedCondition": "ccat5563-h2b-pb-$posHumanName-traffic-$environment"
          },
          {
            "sharedCondition": "h2b-link-contains-id-query-param"
          },
          {
            "sharedCondition": "h2b-friendly-traffic"
          }
        ]
      },
      "experiments": [],
      "result": {
        "weight": 4,
        "metadata": {
          "pageName": ""
        },
        "interceptErrors": true,
        "nerfMode": false,
        "actions": [],
        "redirectIdentifier": "ccat5563-hcom-classic-post-booking-with-encrypted-id-to-hob-trip-overview-$posHumanName-$environment"
      },
      "endpointIdentifier": "hcom-classic-$endpoint"
    }
  }
}
        """.trimIndent()
        return sendCreationRequestAndProcessResponse(authToken, requestBody, ruleIdentifier)
    }

    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Routing-Rules#create-endpoint-rule
     * returns: HTML link to the new rule
     */
    fun createWithItineraryId(authToken: String, posHumanName: String, environment: String, endpoint: String): String {
        val ruleIdentifier = "ccat5563-h2b-$endpoint-to-trip-overview-with-itinerary-id-$posHumanName-$environment"
        val requestBody = """
{
  "query": "mutation addEntity(${'$'}endpointRule: EndpointRuleInput!) { addEndpointRule(endpointRule: ${'$'}endpointRule) { id endpoint { id identifier description } identifier description site { siteIdentifiers siteHostnames } conditions experiments { experimentId shouldLog } result { application { id identifier description } weight metadata { pageName } interceptErrors nerfMode actions } version audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "endpointRule": {
      "identifier": "$ruleIdentifier",
      "description": "Reroutes HCOM Classic $endpoint (with non-encrypted \"itineraryId\") traffic to HoB Trip Overview when PoSa is ${posHumanName.capitalized()} and environment is $environment. More details: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "tenants": [
        "hcom"
      ],
      "site": {
        "siteIdentifiers": [],
        "siteHostnames": [],
        "brand": "BEX"
      },
      "conditions": {
        "and": [
          {
            "sharedCondition": "h2b-hcpb-to-trip-overview-header-with-reroute-value"
          },
          {
            "sharedCondition": "ccat5563-h2b-pb-$posHumanName-traffic-$environment"
          },
          {
            "not": [
              {
                "sharedCondition": "h2b-link-contains-id-query-param"
              }
            ]
          },
          {
            "sharedCondition": "h2b-friendly-traffic"
          }
        ]
      },
      "experiments": [],
      "result": {
        "weight": 3,
        "metadata": {
          "pageName": ""
        },
        "interceptErrors": true,
        "nerfMode": false,
        "actions": [],
        "redirectIdentifier": "ccat5563-hcom-classic-post-booking-with-itinerary-id-to-hob-trip-overview-$posHumanName-$environment"
      },
      "endpointIdentifier": "hcom-classic-$endpoint"
    }
  }
}
        """.trimIndent()
        return sendCreationRequestAndProcessResponse(authToken, requestBody, ruleIdentifier)
    }

    private fun sendCreationRequestAndProcessResponse(authToken: String, requestBody: String, identifier: String): String {
        val response = lobotApiClient.httpPostResponseBody(authToken, requestBody)
        println(response)
        return confluenceHtmlGenerator.generateHtmlLink(RULE_ENTITY_TYPE, response, identifier)
    }

    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Routing-Rules#update-routing-rule
     */
    fun removeTestHeaderValidationFromRuleWithEncryptedId(authToken: String, posHumanName: String, environment: String, endpoint: String) {
        val ruleIdentifier = "ccat5563-h2b-$endpoint-to-trip-overview-with-encrypted-id-$posHumanName-$environment"
        val ruleUpdateParams = ruleUpdateParamsRetriever.retrieve(authToken, ruleIdentifier)
        val requestBody = """
{
  "query": "mutation removeTestHeaderValidationFromRuleWithEncryptedId(${'$'}endpointRule: EndpointRuleInput!, ${'$'}id: Long!) { updateEndpointRule(endpointRule: ${'$'}endpointRule, id: ${'$'}id) { id endpoint { id identifier description } identifier description site { siteIdentifiers siteHostnames } conditions experiments { experimentId shouldLog } result { application { id identifier description } weight metadata { pageName } interceptErrors nerfMode actions } version audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "endpointRule": {
      "identifier": "$ruleIdentifier",
      "description": "Reroutes HCOM Classic $endpoint (with encrypted id) traffic to HoB Trip Overview when PoSa is ${posHumanName.capitalized()} and environment is $environment. More details: https://jira.expedia.biz/browse/CCAT-5563",
      "version": ${ruleUpdateParams.version},
      "tenants": [
        "hcom"
      ],
      "site": {
        "siteIdentifiers": [],
        "siteHostnames": [],
        "brand": "BEX"
      },
      "conditions": {
        "and": [
          {
            "not": [
              {
                "sharedCondition": "hcom-classic-forced-request"
              }
            ]
          },
          {
            "sharedCondition": "ccat5563-h2b-pb-$posHumanName-traffic-$environment"
          },
          {
            "sharedCondition": "h2b-link-contains-id-query-param"
          },
          {
            "sharedCondition": "h2b-friendly-traffic"
          }
        ]
      },
      "experiments": [],
      "result": {
        "weight": 4,
        "metadata": {
          "pageName": ""
        },
        "interceptErrors": true,
        "nerfMode": false,
        "actions": [],
        "redirectIdentifier": "ccat5563-hcom-classic-post-booking-with-encrypted-id-to-hob-trip-overview-$posHumanName-$environment"
      },
      "endpointIdentifier": "hcom-classic-$endpoint"
    },
    "id": "${ruleUpdateParams.id}"
  }
}
        """.trimIndent()
        println(lobotApiClient.httpPostResponseBody(authToken, requestBody))
    }

    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Routing-Rules#update-routing-rule
     */
    fun removeTestHeaderValidationFromRuleWithItineraryId(authToken: String, posHumanName: String, environment: String, endpoint: String) {
        val ruleIdentifier = "ccat5563-h2b-$endpoint-to-trip-overview-with-itinerary-id-$posHumanName-$environment"
        val ruleUpdateParams = ruleUpdateParamsRetriever.retrieve(authToken, ruleIdentifier)
        val requestBody = """
{
  "query": "mutation removeTestHeaderValidationFromRuleWithItineraryId(${'$'}endpointRule: EndpointRuleInput!, ${'$'}id: Long!) { updateEndpointRule(endpointRule: ${'$'}endpointRule, id: ${'$'}id) { id endpoint { id identifier description } identifier description site { siteIdentifiers siteHostnames } conditions experiments { experimentId shouldLog } result { application { id identifier description } weight metadata { pageName } interceptErrors nerfMode actions } version audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "endpointRule": {
      "identifier": "$ruleIdentifier",
      "description": "Reroutes HCOM Classic $endpoint (with non-encrypted \"itineraryId\") traffic to HoB Trip Overview when PoSa is ${posHumanName.capitalized()} and environment is $environment. More details: https://jira.expedia.biz/browse/CCAT-5563",
      "version": ${ruleUpdateParams.version},
      "tenants": [
        "hcom"
      ],
      "site": {
        "siteIdentifiers": [],
        "siteHostnames": [],
        "brand": "BEX"
      },
      "conditions": {
        "and": [
          {
            "not": [
              {
                "sharedCondition": "hcom-classic-forced-request"
              }
            ]
          },
          {
            "sharedCondition": "ccat5563-h2b-pb-$posHumanName-traffic-$environment"
          },
          {
            "not": [
              {
                "sharedCondition": "h2b-link-contains-id-query-param"
              }
            ]
          },
          {
            "sharedCondition": "h2b-friendly-traffic"
          }
        ]
      },
      "experiments": [],
      "result": {
        "weight": 3,
        "metadata": {
          "pageName": ""
        },
        "interceptErrors": true,
        "nerfMode": false,
        "actions": [],
        "redirectIdentifier": "ccat5563-hcom-classic-post-booking-with-itinerary-id-to-hob-trip-overview-$posHumanName-$environment"
      },
      "endpointIdentifier": "hcom-classic-$endpoint"
    },
    "id": "${ruleUpdateParams.id}"
  }
}
        """.trimIndent()
        println(lobotApiClient.httpPostResponseBody(authToken, requestBody))
    }

    fun delete(authToken: String, posHumanName: String, environment: String, endpoint: String, encryptedId: Boolean) {
        val ruleIdentifier = createRuleIdentifier(posHumanName, environment, endpoint, encryptedId)
        val ruleId = ruleUpdateParamsRetriever.getIdForIdentifier(authToken, ruleIdentifier)
        val requestBody = """ 
{
  "query": "mutation deleteRoutingRule(${'$'}id: Long!) { deleteEndpointRule(id: ${'$'}id) { id } }",
  "variables": {
    "id": "$ruleId"
  }
}
        """.trimIndent()
        println(lobotApiClient.httpPostResponseBody(authToken, requestBody))
    }

    private fun createRuleIdentifier(posHumanName: String, environment: String, endpoint: String, encryptedId: Boolean) =
        "ccat5563-h2b-$endpoint-to-trip-overview-with-${ if (encryptedId) "encrypted" else "itinerary" }-id-$posHumanName-$environment"
}
