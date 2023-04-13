package com.expedia.ccat5563

import com.expedia.ccat5563.documentation.ConfluenceHtmlGenerator
import com.expedia.ccat5563.domain.Rerouting
import com.expedia.ccat5563.mutation.RedirectModifier
import com.expedia.ccat5563.mutation.SharedConditionModifier
import com.expedia.ccat5563.mutation.routingrule.RoutingRuleModifier

private const val LAB = "lab"
private const val PROD = "prod"
private const val WEB_VRP_DESKTOP = "web-vrp-desktop"
private const val WEB_VRP_MOBILE = "web-vrp-mobile"
private const val PRINT_RECEIPT = "print-receipt"
private const val WITH_ENCRYPTED_ID = true
private const val WITH_ITINERARY_ID = false

class LobotReroutingEditor(
    private val sharedConditionModifier: SharedConditionModifier,
    private val redirectModifier: RedirectModifier,
    private val routingRuleModifier: RoutingRuleModifier,
    private val confluenceHtmlGenerator: ConfluenceHtmlGenerator
) {
    fun createForPoSas(authToken: String, reroutings: List<Rerouting>) =
        reroutings.forEach{ rerouting -> createForPoSa(authToken, rerouting) }

    /**
     * Creates the necessary Lobot objects for rerouting traffic related to 1 certain POSa,
     * and the corresponding HTML links for documentation purposes.
     *
     * Routing rules with 'itineraryId' query parameter are intentionally NOT created for the print_receipt endpoint,
     * because that endpoint only supports the encrypted 'id' query parameter.
     */
    private fun createForPoSa(authToken: String, rerouting: Rerouting) {
        val oldLabHost = insertStaging1ForLabHost(rerouting.oldProdHost)
        val newLabHost = insertStaging1ForLabHost(rerouting.newProdHost)
        println()
        confluenceHtmlGenerator.generateHtmlRowAndSave(
            rerouting.posHumanName,
            listOf(
                redirectModifier.createWithEncryptedId(authToken, rerouting, LAB, newLabHost),
                redirectModifier.createWithItineraryId(authToken, rerouting, LAB, newLabHost),
                redirectModifier.createWithEncryptedId(authToken, rerouting, PROD, rerouting.newProdHost),
                redirectModifier.createWithItineraryId(authToken, rerouting, PROD, rerouting.newProdHost)
            ),
            listOf(
                sharedConditionModifier.create(authToken, rerouting, oldLabHost, LAB),
                sharedConditionModifier.create(authToken, rerouting, rerouting.oldProdHost, PROD)
            ),
            listOf(
                routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, LAB, WEB_VRP_DESKTOP),
                routingRuleModifier.createWithItineraryId(authToken, rerouting.posHumanName, LAB, WEB_VRP_DESKTOP),
                routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, LAB, WEB_VRP_MOBILE),
                routingRuleModifier.createWithItineraryId(authToken, rerouting.posHumanName, LAB, WEB_VRP_MOBILE),
                routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, LAB, PRINT_RECEIPT),
                routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, PROD, WEB_VRP_DESKTOP),
                routingRuleModifier.createWithItineraryId(authToken, rerouting.posHumanName, PROD, WEB_VRP_DESKTOP),
                routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, PROD, WEB_VRP_MOBILE),
                routingRuleModifier.createWithItineraryId(authToken, rerouting.posHumanName, PROD, WEB_VRP_MOBILE),
                routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, PROD, PRINT_RECEIPT)
            )
        )
    }

    private fun insertStaging1ForLabHost(prodHost: String) : String =
        if (prodHost.endsWith(".hotels.com")) {
            prodHost.removeSuffix(".hotels.com").plus(".staging1-hotels.com")
        } else if (prodHost.endsWith(".hotels.cn")) {
            prodHost.removeSuffix(".hotels.cn").plus(".staging1-hotels.cn")
        } else if (prodHost.endsWith(".hoteles.com")) {
            prodHost.removeSuffix(".hoteles.com").plus(".staging1-hoteles.com")
        } else if (prodHost.endsWith(".hoteis.com")) {
            prodHost.removeSuffix(".hoteis.com").plus(".staging1-hoteis.com")
        } else {
            throw IllegalArgumentException("prodHost has unexpected suffix: $prodHost")
        }

    fun removeTestHeaderValidationFromRoutingRulesForPoSas(authToken: String, reroutings: List<Rerouting>) =
        reroutings.forEach{ rerouting -> removeTestHeaderValidationFromRoutingRulesForPoSa(authToken, rerouting) }

    /**
     * Removes the "h2b-hcpb-to-trip-overview" test header validation from routing rules.
     * (This prepares routing rules for PROD release, but it does not actually promote them to any environment.)
     */
    private fun removeTestHeaderValidationFromRoutingRulesForPoSa(authToken: String, rerouting: Rerouting) {
        routingRuleModifier.removeTestHeaderValidationFromRuleWithEncryptedId(authToken, rerouting.posHumanName, LAB, WEB_VRP_DESKTOP)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithItineraryId(authToken, rerouting.posHumanName, LAB, WEB_VRP_DESKTOP)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithEncryptedId(authToken, rerouting.posHumanName, LAB, WEB_VRP_MOBILE)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithItineraryId(authToken, rerouting.posHumanName, LAB, WEB_VRP_MOBILE)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithEncryptedId(authToken, rerouting.posHumanName, LAB, PRINT_RECEIPT)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithEncryptedId(authToken, rerouting.posHumanName, PROD, WEB_VRP_DESKTOP)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithItineraryId(authToken, rerouting.posHumanName, PROD, WEB_VRP_DESKTOP)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithEncryptedId(authToken, rerouting.posHumanName, PROD, WEB_VRP_MOBILE)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithItineraryId(authToken, rerouting.posHumanName, PROD, WEB_VRP_MOBILE)
        routingRuleModifier.removeTestHeaderValidationFromRuleWithEncryptedId(authToken, rerouting.posHumanName, PROD, PRINT_RECEIPT)
    }

    /**
     * Deletes all routing rules for the specified PoSas.
     * Note: only routing rules are deleted - redirects and shared conditions for the specified PoSas remain untouched!
     */
    fun deleteRulesForPoSas(authToken: String, reroutings: List<Rerouting>) =
        reroutings.forEach{ rerouting -> deleteRulesForPoSa(authToken, rerouting) }

    private fun deleteRulesForPoSa(authToken: String, rerouting: Rerouting) {
        routingRuleModifier.delete(authToken, rerouting.posHumanName, LAB, WEB_VRP_DESKTOP, WITH_ENCRYPTED_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, LAB, WEB_VRP_DESKTOP, WITH_ITINERARY_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, LAB, WEB_VRP_MOBILE, WITH_ENCRYPTED_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, LAB, WEB_VRP_MOBILE, WITH_ITINERARY_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, LAB, PRINT_RECEIPT, WITH_ENCRYPTED_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, PROD, WEB_VRP_DESKTOP, WITH_ENCRYPTED_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, PROD, WEB_VRP_DESKTOP, WITH_ITINERARY_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, PROD, WEB_VRP_MOBILE, WITH_ENCRYPTED_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, PROD, WEB_VRP_MOBILE, WITH_ITINERARY_ID)
        routingRuleModifier.delete(authToken, rerouting.posHumanName, PROD, PRINT_RECEIPT, WITH_ENCRYPTED_ID)
    }
}
