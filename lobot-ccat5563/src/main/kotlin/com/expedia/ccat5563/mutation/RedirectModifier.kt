package com.expedia.ccat5563.mutation

import com.expedia.ccat5563.client.LobotApiClient
import com.expedia.ccat5563.domain.Rerouting

class RedirectModifier(
    private val lobotApiClient: LobotApiClient
) {
    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Redirects#create-redirect
     */
    fun createWithEncryptedId(authToken: String, rerouting: Rerouting, environment: String, newHost: String) {
        val requestBody = """
{
  "query": "mutation addEntity(${'$'}redirect: RedirectInput!) { addRedirect(redirect: ${'$'}redirect) { id identifier description version statusCode keepQuery urlPattern replacement owners { email } audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "redirect": {
      "identifier": "ccat5563-hcom-classic-post-booking-with-encrypted-id-to-hob-trip-overview-${rerouting.posHumanName}-$environment",
      "description": "Redirects HCOM Classic View Reservation Page and Print Receipt URLs to HoB Trip OverView when encrypted id query parameter is present in the URL and environment is ${environment}, setting pos=${rerouting.newPos} and locale=${rerouting.newLocale}. More info: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "urlPattern": "^.+?(\\?|&)id=(.[^&]+)",
      "replacement": "https://$newHost/trips/hcom_eid_${'$'}2?pos=${rerouting.newPos}&locale=${rerouting.newLocale}",
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
        println(lobotApiClient.httpPostResponseBody(authToken, requestBody))
    }

    /**
     * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Redirects#create-redirect
     */
    fun createWithItineraryId(authToken: String, rerouting: Rerouting, environment: String, newHost: String) {
        val requestBody = """
{
  "query": "mutation addEntity(${'$'}redirect: RedirectInput!) { addRedirect(redirect: ${'$'}redirect) { id identifier description version statusCode keepQuery urlPattern replacement owners { email } audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "redirect": {
      "identifier": "ccat5563-hcom-classic-post-booking-with-itinerary-id-to-hob-trip-overview-${rerouting.posHumanName}-$environment",
      "description": "Redirects HCOM Classic View Reservation Page and Print Receipt URLs to HoB Trip OverView when non-encrypted \"itineraryId\" query parameter is present in the URL and environment is $environment, setting pos=${rerouting.newPos} and locale=${rerouting.newLocale}. More info: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "urlPattern": "^.+?(\\?|&)itineraryId=([0-9]+)",
      "replacement": "https://$newHost/trips/${'$'}2?pos=${rerouting.newPos}&locale=${rerouting.newLocale}",
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
        println(lobotApiClient.httpPostResponseBody(authToken, requestBody))
    }
}
