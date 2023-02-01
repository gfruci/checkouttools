package com.expedia.ccat5563.domain

data class Rerouting(
    val posHumanName: String,
    val oldPos: String,
    val oldLocale: String,
    val oldProdHost: String,
    val newPos: String,
    val newLocale: String,
    val newProdHost: String
)
