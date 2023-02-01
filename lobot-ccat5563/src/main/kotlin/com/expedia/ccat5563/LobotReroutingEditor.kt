package com.expedia.ccat5563

import com.expedia.ccat5563.domain.Rerouting
import com.expedia.ccat5563.mutation.RedirectModifier
import com.expedia.ccat5563.mutation.SharedConditionModifier
import com.expedia.ccat5563.mutation.routingrule.RoutingRuleModifier

private const val LAB = "lab"
private const val PROD = "prod"
private const val WEB_VRP_DESKTOP = "web-vrp-desktop"
private const val WEB_VRP_MOBILE = "web-vrp-mobile"
private const val PRINT_RECEIPT = "print-receipt"

class LobotReroutingEditor(
    private val sharedConditionModifier: SharedConditionModifier,
    private val redirectModifier: RedirectModifier,
    private val routingRuleModifier: RoutingRuleModifier
) {
    fun createForPoSas(authToken: String, reroutings: List<Rerouting>) =
        reroutings.forEach{ rerouting -> createForPoSa(authToken, rerouting) }

    /**
     * Creates the necessary Lobot objects for rerouting traffic related to 1 certain POSa.
     *
     * Routing rules with 'itineraryId' query parameter are intentionally NOT created for the print_receipt endpoint,
     * because that endpoint only supports the encrypted 'id' query parameter.
     */
    private fun createForPoSa(authToken: String, rerouting: Rerouting) {
        val oldLabHost = insertStaging1ForLabHost(rerouting.oldProdHost)
        val newLabHost = insertStaging1ForLabHost(rerouting.newProdHost)
        println()
        sharedConditionModifier.create(authToken, rerouting, oldLabHost)
        redirectModifier.createWithEncryptedId(authToken, rerouting, LAB, newLabHost)
        redirectModifier.createWithItineraryId(authToken, rerouting, LAB, newLabHost)
        redirectModifier.createWithEncryptedId(authToken, rerouting, PROD, rerouting.newProdHost)
        redirectModifier.createWithItineraryId(authToken, rerouting, PROD, rerouting.newProdHost)
        routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, LAB, WEB_VRP_DESKTOP)
        routingRuleModifier.createWithItineraryId(authToken, rerouting.posHumanName, LAB, WEB_VRP_DESKTOP)
        routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, LAB, WEB_VRP_MOBILE)
        routingRuleModifier.createWithItineraryId(authToken, rerouting.posHumanName, LAB, WEB_VRP_MOBILE)
        routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, LAB, PRINT_RECEIPT)
        routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, PROD, WEB_VRP_DESKTOP)
        routingRuleModifier.createWithItineraryId(authToken, rerouting.posHumanName, PROD, WEB_VRP_DESKTOP)
        routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, PROD, WEB_VRP_MOBILE)
        routingRuleModifier.createWithItineraryId(authToken, rerouting.posHumanName, PROD, WEB_VRP_MOBILE)
        routingRuleModifier.createWithEncryptedId(authToken, rerouting.posHumanName, PROD, PRINT_RECEIPT)
    }

    private fun insertStaging1ForLabHost(prodHost: String) : String =
        if (prodHost.endsWith(".hotels.com")) {
            prodHost.removeSuffix(".hotels.com").plus(".staging1-hotels.com")
        } else if (prodHost.endsWith(".hotels.cn")) {
            prodHost.removeSuffix(".hotels.cn").plus(".staging1-hotels.cn")
        } else if (prodHost.endsWith(".hoteles.com")) {
            prodHost.removeSuffix(".hoteles.com").plus(".staging1-hoteles.com")
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
}
