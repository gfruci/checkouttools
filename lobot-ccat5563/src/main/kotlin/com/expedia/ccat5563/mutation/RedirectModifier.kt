package com.expedia.ccat5563.mutation

import com.expedia.ccat5563.client.LobotApiClient
import com.expedia.ccat5563.documentation.ConfluenceHtmlGenerator
import com.expedia.ccat5563.domain.Rerouting

private const val REDIRECT_ENTITY_TYPE = "redirects"
private const val EMPTY = ""

class RedirectModifier(
    private val lobotApiClient: LobotApiClient,
    private val confluenceHtmlGenerator: ConfluenceHtmlGenerator
) {
    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Redirects#create-redirect
     * returns: HTML link to the new redirect
     */
    fun createWithEncryptedId(authToken: String, rerouting: Rerouting, environment: String, newHost: String): String {
        val redirectIdentifier = "ccat5563-hcom-classic-post-booking-with-encrypted-id-to-hob-trip-overview-${rerouting.posHumanName}-$environment"
        val requestBody = """
{
  "query": "mutation addEntity(${'$'}redirect: RedirectInput!) { addRedirect(redirect: ${'$'}redirect) { id identifier description version statusCode keepQuery urlPattern replacement owners { email } audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "redirect": {
      "identifier": "$redirectIdentifier",
      "description": "Redirects HCOM Classic View Reservation Page and Print Receipt URLs to HoB Trip OverView when encrypted id query parameter is present in the URL and environment is ${environment}, setting pos=${rerouting.newPos} and locale=${rerouting.newLocale}. More info: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "urlPattern": "^.+?(\\?|&)id=(.[^&]+)",
      "replacement": "https://$newHost/trips/hcom_eid_${'$'}2?pos=${rerouting.newPos}&locale=${rerouting.newLocale}${getOptionalSiteId(rerouting.newSiteId)}",
      "keepQuery": false,
      "statusCode": 301,
      "owners": [
        {
          "email": "HcomTechCCAT@expedia.com",
          "name": "",
          "phone": null,
          "chat": null,
          "pagerduty": null,
          "notes": null,
          "repo": null
        }
      ]
    }
  }
}
        """.trimIndent()
        return sendCreationRequestAndProcessResponse(authToken, requestBody, redirectIdentifier)
    }

    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Redirects#create-redirect
     * returns: HTML link to the new redirect
     */
    fun createWithItineraryId(authToken: String, rerouting: Rerouting, environment: String, newHost: String): String {
        val redirectIdentifier = "ccat5563-hcom-classic-post-booking-with-itinerary-id-to-hob-trip-overview-${rerouting.posHumanName}-$environment"
        val requestBody = """
{
  "query": "mutation addEntity(${'$'}redirect: RedirectInput!) { addRedirect(redirect: ${'$'}redirect) { id identifier description version statusCode keepQuery urlPattern replacement owners { email } audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "redirect": {
      "identifier": "$redirectIdentifier",
      "description": "Redirects HCOM Classic View Reservation Page and Print Receipt URLs to HoB Trip OverView when non-encrypted \"itineraryId\" query parameter is present in the URL and environment is $environment, setting pos=${rerouting.newPos} and locale=${rerouting.newLocale}. More info: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "urlPattern": "^.+?(\\?|&)itineraryId=([0-9]+)",
      "replacement": "https://$newHost/trips/${'$'}2?pos=${rerouting.newPos}&locale=${rerouting.newLocale}${getOptionalSiteId(rerouting.newSiteId)}",
      "keepQuery": false,
      "statusCode": 301,
      "owners": [
        {
          "email": "HcomTechCCAT@expedia.com",
          "name": "",
          "phone": null,
          "chat": null,
          "pagerduty": null,
          "notes": null,
          "repo": null
        }
      ]
    }
  }
}
        """.trimIndent()
        return sendCreationRequestAndProcessResponse(authToken, requestBody, redirectIdentifier)
    }

    private fun getOptionalSiteId(siteId: String?) =
        siteId?.let{"&siteid=$it"} ?: EMPTY

    private fun sendCreationRequestAndProcessResponse(authToken: String, requestBody: String, identifier: String): String {
        val response = lobotApiClient.httpPostResponseBody(authToken, requestBody)
        println(response)
        return confluenceHtmlGenerator.generateHtmlLink(REDIRECT_ENTITY_TYPE, response, identifier)
    }
}
