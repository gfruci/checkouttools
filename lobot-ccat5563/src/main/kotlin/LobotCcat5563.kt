import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

fun basicAuthToken(usernm: String, passwd: String) : String =
    "${usernm}:${passwd}".toByteArray().let(Base64.getEncoder()::encodeToString)

fun httpBasicPostResponseBody(url: String, basicAuthToken: String, requestBody: String) : String {
    val response = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .uri(URI.create(url))
        .header("Authorization", "Basic ${basicAuthToken}")
        .header("Content-Type", "application/json")
        .build(),
        HttpResponse.BodyHandlers.ofString())
    return response.body()
}

/**
 * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Highlander-API#access
 */
fun authToken(url: String, usernm: String, passwd: String) : String {
    val basicAuthToken = basicAuthToken(usernm, passwd)
    val requestBody = """{"query": "query getToken { getToken { token } }"}"""
    return httpBasicPostResponseBody(url, basicAuthToken, requestBody)
        .removePrefix("""{"data":{"getToken":{"token":"""")
        .removeSuffix(""""}}}""")
}

fun httpPostResponseBody(url: String, authToken: String, requestBody: String) : String {
    val response = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .uri(URI.create(url))
        .header("Authorization", "Bearer ${authToken}")
        .header("Content-Type", "application/json")
        .build(),
        HttpResponse.BodyHandlers.ofString())
    return response.body()
}

/**
 * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Shared-Conditions#create-shared-condition
 */
fun createSharedCondition(url: String, authToken: String, posHumanName: String,
    oldPos: String, oldLocale: String, oldLabHost: String, oldProdHost: String) {
    val requestBody = """
{
  "query": "mutation addEntity(${'$'}sharedCondition: SharedConditionInput!) { addSharedCondition(sharedCondition: ${'$'}sharedCondition) { id identifier description version conditions audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "sharedCondition": {
      "identifier": "ccat5563-h2b-pb-${posHumanName}-traffic",
      "description": "Evaluates whether the HCOM Classic post-booking traffic (to be rerouted to HoB Trip Overview page) comes from ${oldPos} pos with ${oldLocale} locale. More details: https://jira.expedia.biz/browse/CCAT-5563",
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
                  "value": "${oldProdHost}"
                }
              },
              {
                "header": {
                  "name": "Host",
                  "match": "EQUAL_IGNORE_CASE",
                  "type": "request",
                  "value": "${oldLabHost}"
                }
              }
            ]
          },
          {
            "query": {
              "name": "pos",
              "match": "EQUAL_IGNORE_CASE",
              "value": "${oldPos}"
            }
          },
          {
            "query": {
              "name": "locale",
              "match": "EQUAL_IGNORE_CASE",
              "value": "${oldLocale}"
            }
          }
        ]
      }
    }
  }
}            
        """.trimIndent()
    println(httpPostResponseBody(url, authToken, requestBody))
}

/**
 * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Redirects#create-redirect
 */
fun createRedirectWithEncryptedId(url: String, authToken: String, posHumanName: String,
    environment: String, newPos: String, newLocale: String, newHost: String) {
    val requestBody = """
{
  "query": "mutation addEntity(${'$'}redirect: RedirectInput!) { addRedirect(redirect: ${'$'}redirect) { id identifier description version statusCode keepQuery urlPattern replacement owners { email } audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "redirect": {
      "identifier": "ccat5563-hcom-classic-post-booking-with-encrypted-id-to-hob-trip-overview-${posHumanName}-${environment}",
      "description": "Redirects HCOM Classic View Reservation Page and Print Receipt URLs to HoB Trip OverView when encrypted id query parameter is present in the URL and environment is ${environment}, setting pos=${newPos} and locale=${newLocale}. More info: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "urlPattern": "^.+?(\\?|&)id=(.[^&]+)",
      "replacement": "https://${newHost}/trips/hcom_eid_${'$'}2?pos=${newPos}&locale=${newLocale}",
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
    println(httpPostResponseBody(url, authToken, requestBody))
}

/**
 * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Redirects#create-redirect
 */
fun createRedirectWithItineraryId(url: String, authToken: String, posHumanName: String,
    environment: String, newPos: String, newLocale: String, newHost: String) {
    val requestBody = """
{
  "query": "mutation addEntity(${'$'}redirect: RedirectInput!) { addRedirect(redirect: ${'$'}redirect) { id identifier description version statusCode keepQuery urlPattern replacement owners { email } audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "redirect": {
      "identifier": "ccat5563-hcom-classic-post-booking-with-itinerary-id-to-hob-trip-overview-${posHumanName}-${environment}",
      "description": "Redirects HCOM Classic View Reservation Page and Print Receipt URLs to HoB Trip OverView when non-encrypted \"itineraryId\" query parameter is present in the URL and environment is ${environment}, setting pos=${newPos} and locale=${newLocale}. More info: https://jira.expedia.biz/browse/CCAT-5563",
      "version": 1,
      "urlPattern": "^.+?(\\?|&)itineraryId=([0-9]+)",
      "replacement": "https://${newHost}/trips/${'$'}2?pos=${newPos}&locale=${newLocale}",
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
    println(httpPostResponseBody(url, authToken, requestBody))
}

/**
 * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Routing-Rules#create-endpoint-rule
 */
fun createRoutingRuleWithEncryptedId(url: String, authToken: String, posHumanName: String,
    environment: String, endpoint: String) {
    val requestBody = """
{
  "query": "mutation addEntity(${'$'}endpointRule: EndpointRuleInput!) { addEndpointRule(endpointRule: ${'$'}endpointRule) { id endpoint { id identifier description } identifier description site { siteIdentifiers siteHostnames } conditions experiments { experimentId shouldLog } result { application { id identifier description } weight metadata { pageName } interceptErrors nerfMode actions } version audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "endpointRule": {
      "identifier": "ccat5563-h2b-${endpoint}-to-trip-overview-with-encrypted-id-${posHumanName}-${environment}",
      "description": "Reroutes HCOM Classic ${endpoint} (with encrypted id) traffic to HoB Trip Overview when PoSa is ${posHumanName.capitalized()} and environment is ${environment}. More details: https://jira.expedia.biz/browse/CCAT-5563",
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
            "sharedCondition": "ccat5563-h2b-pb-${posHumanName}-traffic"
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
        "redirectIdentifier": "ccat5563-hcom-classic-post-booking-with-encrypted-id-to-hob-trip-overview-${posHumanName}-${environment}"
      },
      "endpointIdentifier": "hcom-classic-${endpoint}"
    }
  }
}
        """.trimIndent()
    println(httpPostResponseBody(url, authToken, requestBody))
}

/**
 * https://github.expedia.biz/Brand-Expedia/lobot-api-java/wiki/Routing-Rules#create-endpoint-rule
 */
fun createRoutingRuleWithItineraryId(url: String, authToken: String, posHumanName: String,
    environment: String, endpoint: String) {
    val requestBody = """
{
  "query": "mutation addEntity(${'$'}endpointRule: EndpointRuleInput!) { addEndpointRule(endpointRule: ${'$'}endpointRule) { id endpoint { id identifier description } identifier description site { siteIdentifiers siteHostnames } conditions experiments { experimentId shouldLog } result { application { id identifier description } weight metadata { pageName } interceptErrors nerfMode actions } version audit { createdAt createdBy lastUpdatedAt lastUpdatedBy } } }",
  "variables": {
    "endpointRule": {
      "identifier": "ccat5563-h2b-${endpoint}-to-trip-overview-with-itinerary-id-${posHumanName}-${environment}",
      "description": "Reroutes HCOM Classic ${endpoint} (with non-encrypted \"itineraryId\") traffic to HoB Trip Overview when PoSa is ${posHumanName.capitalized()} and environment is ${environment}. More details: https://jira.expedia.biz/browse/CCAT-5563",
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
            "sharedCondition": "ccat5563-h2b-pb-${posHumanName}-traffic"
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
        "redirectIdentifier": "ccat5563-hcom-classic-post-booking-with-itinerary-id-to-hob-trip-overview-${posHumanName}-${environment}"
      },
      "endpointIdentifier": "hcom-classic-${endpoint}"
    }
  }
}
        """.trimIndent()
    println(httpPostResponseBody(url, authToken, requestBody))
}

fun insertStaging1ForLabHost(prodHost: String) : String =
    if (prodHost.endsWith(".hotels.com")) {
        prodHost.removeSuffix(".hotels.com").plus(".staging1-hotels.com")
    } else if (prodHost.endsWith(".hotels.cn")) {
        prodHost.removeSuffix(".hotels.cn").plus(".staging1-hotels.cn")
    } else if (prodHost.endsWith(".hoteles.com")) {
        prodHost.removeSuffix(".hoteles.com").plus(".staging1-hoteles.com")
    } else {
        throw IllegalArgumentException("prodHost has unexpected suffix: ${prodHost}")
    }

/**
 * Creates the necessary Lobot objects for rerouting traffic related to 1 certain POSa.
 *
 * Routing rules with 'itineraryId' query parameter are intentionally NOT created for the print_receipt endpoint,
 * because that endpoint only supports the encrypted 'id' query parameter.
 */
fun createForPoSa(url: String, authToken: String, posHumanName: String,
    oldPos: String, oldLocale: String, oldProdHost: String,
    newPos: String, newLocale: String, newProdHost: String) {
    val oldLabHost = insertStaging1ForLabHost(oldProdHost)
    val newLabHost = insertStaging1ForLabHost(newProdHost)
    println()
    createSharedCondition(url, authToken, posHumanName, oldPos, oldLocale, oldLabHost, oldProdHost)
    createRedirectWithEncryptedId(url, authToken, posHumanName, "lab", newPos, newLocale, newLabHost)
    createRedirectWithItineraryId(url, authToken, posHumanName, "lab", newPos, newLocale, newLabHost)
    createRedirectWithEncryptedId(url, authToken, posHumanName, "prod", newPos, newLocale, newProdHost)
    createRedirectWithItineraryId(url, authToken, posHumanName, "prod", newPos, newLocale, newProdHost)
    createRoutingRuleWithEncryptedId(url, authToken, posHumanName, "lab", "web-vrp-desktop")
    createRoutingRuleWithItineraryId(url, authToken, posHumanName, "lab", "web-vrp-desktop")
    createRoutingRuleWithEncryptedId(url, authToken, posHumanName, "lab", "web-vrp-mobile")
    createRoutingRuleWithItineraryId(url, authToken, posHumanName, "lab", "web-vrp-mobile")
    createRoutingRuleWithEncryptedId(url, authToken, posHumanName, "lab", "print-receipt")
    createRoutingRuleWithEncryptedId(url, authToken, posHumanName, "prod", "web-vrp-desktop")
    createRoutingRuleWithItineraryId(url, authToken, posHumanName, "prod", "web-vrp-desktop")
    createRoutingRuleWithEncryptedId(url, authToken, posHumanName, "prod", "web-vrp-mobile")
    createRoutingRuleWithItineraryId(url, authToken, posHumanName, "prod", "web-vrp-mobile")
    createRoutingRuleWithEncryptedId(url, authToken, posHumanName, "prod", "print-receipt")
}

fun createForPoSas(url: String, authToken: String) {
    createForPoSa(url, authToken, "belgium-dutch",
        "HCOM_BE", "nl_BE","nl.hotels.com",
        "HCOM_BE", "nl_BE", "be.hotels.com")
    createForPoSa(url, authToken, "belgium-german",
        "HCOM_BE", "de_BE","de.hotels.com",
        "HCOM_BE", "de_BE", "be.hotels.com")
    createForPoSa(url, authToken, "belgium-french",
        "HCOM_BE", "fr_BE","fr.hotels.com",
        "HCOM_BE", "fr_BE", "be.hotels.com")
    createForPoSa(url, authToken, "belize",
        "HCOM_LATAM", "es_BZ", "www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "bolivia",
        "HCOM_LATAM", "es_BO","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "canada-french",
        "HCOM_CA", "fr_CA","fr.hotels.com",
        "HCOM_CA", "fr_CA", "ca.hotels.com")
    createForPoSa(url, authToken, "czech",
        "HCOM_CZ", "cs_CZ","cs.hotels.com",
        "HCOM_EMEA", "en_IE", "www.hotels.com")
    createForPoSa(url, authToken, "china-english",
        "HCOM_ASIA", "en_CN","www.hotels.com",
        "HCOM_CN", "en_US", "www.hotels.cn")
    createForPoSa(url, authToken, "united-states-spanish",
        "HCOM_US", "es_US","es.hotels.com",
        "HCOM_US", "es_US", "www.hotels.com")
    createForPoSa(url, authToken, "guatemala",
        "HCOM_LATAM", "es_GT","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "guyana",
        "HCOM_LATAM", "es_GY","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "guyana-french",
        "HCOM_LATAM", "fr_GF","fr.hotels.com",
        "HCOM_FR", "fr_FR", "fr.hotels.com")
    createForPoSa(url, authToken, "honduras",
        "HCOM_LATAM", "es_HN","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "hong-kong-english",
        "HCOM_ASIA", "en_HK","www.hotels.com",
        "HCOM_HK", "en_HK", "zh.hotels.com")
    createForPoSa(url, authToken, "indonesia-bahasa",
        "HCOM_ID", "in_ID","id.hotels.com",
        "HCOM_ID", "id_ID", "id.hotels.com")
    createForPoSa(url, authToken, "indonesia-english",
        "HCOM_ASIA", "en_ID","www.hotels.com",
        "HCOM_ID", "en_GB", "id.hotels.com")
    createForPoSa(url, authToken, "israel-english",
        "HCOM_IL", "en_IL","he.hotels.com",
        "HCOM_ME", "en_GB", "www.hotels.com")
    createForPoSa(url, authToken, "israel-hebrew",
        "HCOM_IL", "iw_IL","he.hotels.com",
        "HCOM_ME", "en_GB", "www.hotels.com")
    createForPoSa(url, authToken, "japan-english",
        "HCOM_ASIA", "en_JP","www.hotels.com",
        "HCOM_JP", "en_US", "jp.hotels.com")
    createForPoSa(url, authToken, "malaysia-english",
        "HCOM_ASIA", "en_MY","www.hotels.com",
        "HCOM_MY", "en_MY", "ms.hotels.com")
    createForPoSa(url, authToken, "mexico-english",
        "HCOM_LATAM", "en_MX","www.hotels.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "nicaragua",
        "HCOM_LATAM", "es_NI","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "norway",
        "HCOM_NO", "no_NO","no.hotels.com",
        "HCOM_NO", "nb_NO", "no.hotels.com")
    createForPoSa(url, authToken, "austria",
        "HCOM_AT", "de_AT","at.hotels.com",
        "HCOM_AT", "de_DE", "at.hotels.com")
    createForPoSa(url, authToken, "paraguay",
        "HCOM_LATAM", "es_PY","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "poland",
        "HCOM_PL", "pl_PL","pl.hotels.com",
        "HCOM_EMEA", "en_IE", "www.hotels.com")
    createForPoSa(url, authToken, "rest-of-latam-english",
        "HCOM_LATAM", "en_LA","www.hotels.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "slovakia",
        "HCOM_SK", "sk_SK","sk.hotels.com",
        "HCOM_EMEA", "en_IE", "www.hotels.com")
    createForPoSa(url, authToken, "south-korea-english",
        "HCOM_ASIA", "en_KR","www.hotels.com",
        "HCOM_KR", "en_US", "kr.hotels.com")
    createForPoSa(url, authToken, "suriname-dutch",
        "HCOM_LATAM", "nl_SR","nl.hotels.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "switzerland-french",
        "HCOM_CH", "fr_CH","fr.hotels.com",
        "HCOM_CH", "fr_CH", "ch.hotels.com")
    createForPoSa(url, authToken, "switzerland-italian",
        "HCOM_CH", "it_CH","it.hotels.com",
        "HCOM_CH", "it_CH", "ch.hotels.com")
    createForPoSa(url, authToken, "taiwan-english",
        "HCOM_ASIA", "en_TW","www.hotels.com",
        "HCOM_TW", "en_US", "tw.hotels.com")
    createForPoSa(url, authToken, "thailand-english",
        "HCOM_ASIA", "en_TH","www.hotels.com",
        "HCOM_TH", "en_GB", "th.hotels.com")
    createForPoSa(url, authToken, "the-middle-east-english",
        "HCOM_ME", "en_IE","www.hotels.com",
        "HCOM_ME", "en_GB", "www.hotels.com")
    createForPoSa(url, authToken, "uae-the-middle-east-arabic",
        "HCOM_ARABIC", "ar_AE","ar.hotels.com",
        "HCOM_ME", "en_GB", "www.hotels.com")
    createForPoSa(url, authToken, "ukraine",
        "HCOM_UA", "uk_UA","ua.hotels.com",
        "HCOM_EMEA", "en_IE", "www.hotels.com")
    createForPoSa(url, authToken, "uruguay",
        "HCOM_LATAM", "es_UY","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
    createForPoSa(url, authToken, "vietnam-english",
        "HCOM_ASIA", "en_VN","www.hotels.com",
        "HCOM_VN", "en_GB", "vi.hotels.com")
}

/**
 * Main entry point.
 * https://jira.expedia.biz/browse/CCAT-5563
 * https://confluence.expedia.biz/pages/viewpage.action?spaceKey=LCB2&title=Lobot+objects+created+for+Booking+Management+rerouting
 */
fun main(args: Array<String>) {
    val url = "https://lobot-api-java.us-east-1.prod.expedia.com/highlander"
    val seaUsername = args[0]
    val seaPassword = args[1]
    val authToken = authToken(url, seaUsername, seaPassword)
    println("authToken=${authToken}")

    createForPoSa(url, authToken, "mexico-english",
        "HCOM_LATAM", "en_MX","www.hotels.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com")
}
