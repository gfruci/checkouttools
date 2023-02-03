package com.expedia.ccat5563

import com.expedia.ccat5563.client.LobotApiClient
import com.expedia.ccat5563.domain.Rerouting
import com.expedia.ccat5563.mutation.RedirectModifier
import com.expedia.ccat5563.mutation.SharedConditionModifier
import com.expedia.ccat5563.mutation.routingrule.RuleUpdateParamsRetriever
import com.expedia.ccat5563.mutation.routingrule.RoutingRuleModifier

private const val FIRST_INDEX = 0
private const val SECOND_INDEX = 1

private val REROUTINGS = listOf(
    Rerouting("belgium-dutch",
        "HCOM_BE", "nl_BE","nl.hotels.com",
        "HCOM_BE", "nl_BE", "be.hotels.com"),
    Rerouting("belgium-german",
        "HCOM_BE", "de_BE","de.hotels.com",
        "HCOM_BE", "de_BE", "be.hotels.com"),
    Rerouting("belgium-french",
        "HCOM_BE", "fr_BE","fr.hotels.com",
        "HCOM_BE", "fr_BE", "be.hotels.com"),
    Rerouting("belize",
        "HCOM_LATAM", "es_BZ", "www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("bolivia",
        "HCOM_LATAM", "es_BO","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("canada-french",
        "HCOM_CA", "fr_CA","fr.hotels.com",
        "HCOM_CA", "fr_CA", "ca.hotels.com"),
    Rerouting("czech",
        "HCOM_CZ", "cs_CZ","cs.hotels.com",
        "HCOM_EMEA", "en_IE", "www.hotels.com"),
    Rerouting("china-english",
        "HCOM_ASIA", "en_CN","www.hotels.com",
        "HCOM_CN", "en_US", "www.hotels.cn"),
    Rerouting("united-states-spanish",
        "HCOM_US", "es_US","es.hotels.com",
        "HCOM_US", "es_US", "www.hotels.com"),
    Rerouting("guatemala",
        "HCOM_LATAM", "es_GT","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("guyana",
        "HCOM_LATAM", "es_GY","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("guyana-french",
        "HCOM_LATAM", "fr_GF","fr.hotels.com",
        "HCOM_FR", "fr_FR", "fr.hotels.com"),
    Rerouting("honduras",
        "HCOM_LATAM", "es_HN","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("hong-kong-english",
        "HCOM_ASIA", "en_HK","www.hotels.com",
        "HCOM_HK", "en_HK", "zh.hotels.com"),
    Rerouting("indonesia-bahasa",
        "HCOM_ID", "in_ID","id.hotels.com",
        "HCOM_ID", "id_ID", "id.hotels.com"),
    Rerouting("indonesia-english",
        "HCOM_ASIA", "en_ID","www.hotels.com",
        "HCOM_ID", "en_GB", "id.hotels.com"),
    Rerouting("israel-english",
        "HCOM_IL", "en_IL","he.hotels.com",
        "HCOM_ME", "en_GB", "www.hotels.com"),
    Rerouting("israel-hebrew",
        "HCOM_IL", "iw_IL","he.hotels.com",
        "HCOM_ME", "en_GB", "www.hotels.com"),
    Rerouting("japan-english",
        "HCOM_ASIA", "en_JP","www.hotels.com",
        "HCOM_JP", "en_US", "jp.hotels.com"),
    Rerouting("malaysia-english",
        "HCOM_ASIA", "en_MY","www.hotels.com",
        "HCOM_MY", "en_MY", "ms.hotels.com"),
    Rerouting("mexico-english",
        "HCOM_LATAM", "en_MX","www.hotels.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("nicaragua",
        "HCOM_LATAM", "es_NI","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("norway",
        "HCOM_NO", "no_NO","no.hotels.com",
        "HCOM_NO", "nb_NO", "no.hotels.com"),
    Rerouting("austria",
        "HCOM_AT", "de_AT","at.hotels.com",
        "HCOM_AT", "de_DE", "at.hotels.com"),
    Rerouting("paraguay",
        "HCOM_LATAM", "es_PY","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("poland",
        "HCOM_PL", "pl_PL","pl.hotels.com",
        "HCOM_EMEA", "en_IE", "www.hotels.com"),
    Rerouting("rest-of-latam-english",
        "HCOM_LATAM", "en_LA","www.hotels.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("slovakia",
        "HCOM_SK", "sk_SK","sk.hotels.com",
        "HCOM_EMEA", "en_IE", "www.hotels.com"),
    Rerouting("south-korea-english",
        "HCOM_ASIA", "en_KR","www.hotels.com",
        "HCOM_KR", "en_US", "kr.hotels.com"),
    Rerouting("suriname-dutch",
        "HCOM_LATAM", "nl_SR","nl.hotels.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("switzerland-french",
        "HCOM_CH", "fr_CH","fr.hotels.com",
        "HCOM_CH", "fr_CH", "ch.hotels.com"),
    Rerouting("switzerland-italian",
        "HCOM_CH", "it_CH","it.hotels.com",
        "HCOM_CH", "it_CH", "ch.hotels.com"),
    Rerouting("taiwan-english",
        "HCOM_ASIA", "en_TW","www.hotels.com",
        "HCOM_TW", "en_US", "tw.hotels.com"),
    Rerouting("thailand-english",
        "HCOM_ASIA", "en_TH","www.hotels.com",
        "HCOM_TH", "en_GB", "th.hotels.com"),
    Rerouting("the-middle-east-english",
        "HCOM_ME", "en_IE","www.hotels.com",
        "HCOM_ME", "en_GB", "www.hotels.com"),
    Rerouting("uae-the-middle-east-arabic",
        "HCOM_ARABIC", "ar_AE","ar.hotels.com",
        "HCOM_ME", "en_GB", "www.hotels.com"),
    Rerouting("ukraine",
        "HCOM_UA", "uk_UA","ua.hotels.com",
        "HCOM_EMEA", "en_IE", "www.hotels.com"),
    Rerouting("uruguay",
        "HCOM_LATAM", "es_UY","www.hoteles.com",
        "HCOM_LATAM", "en_US", "www.hoteles.com"),
    Rerouting("vietnam-english",
        "HCOM_ASIA", "en_VN","www.hotels.com",
        "HCOM_VN", "en_GB", "vi.hotels.com")
)

/**
 * Main entry point.
 * https://jira.expedia.biz/browse/CCAT-5563
 * https://confluence.expedia.biz/pages/viewpage.action?spaceKey=LCB2&title=Lobot+objects+created+for+Booking+Management+rerouting
 */
fun main(args: Array<String>) {
    val lobotApiClient = LobotApiClient()
    val lobotReroutingEditor = LobotReroutingEditor(
        SharedConditionModifier(lobotApiClient),
        RedirectModifier(lobotApiClient),
        RoutingRuleModifier(lobotApiClient, RuleUpdateParamsRetriever(lobotApiClient))
    )
    val authToken = getAuthToken(lobotApiClient, args)

    /**
     * Run this to create shared condition, redirects, routing rules for all rerouting scenarios in REROUTINGS.
     */
//    lobotReroutingEditor.createForPoSas(authToken, REROUTINGS)

    /**
     * Run this to prepare routing rules for PROD release: removes the "h2b-hcpb-to-trip-overview" test header validation from them.
     * (This does not include the promotion of the changes to any environment.)
     */
//    lobotReroutingEditor.removeTestHeaderValidationFromRoutingRulesForPoSas(authToken, REROUTINGS)
}

private fun getAuthToken(lobotApiClient: LobotApiClient, args: Array<String>): String {
    val seaUsername = args[FIRST_INDEX]
    val seaPassword = args[SECOND_INDEX]
    val authToken = AuthTokenProvider(lobotApiClient).provide(seaUsername, seaPassword)
    println("authToken=${authToken}")
    return authToken
}
