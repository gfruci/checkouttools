package com.expedia.ccat5563

import java.util.Base64
import com.expedia.ccat5563.client.LobotApiClient

private const val BASIC_AUTHORIZATION = true

class AuthTokenProvider(
    private val lobotApiClient: LobotApiClient
) {
    fun provide(userName: String, password: String) : String {
        val basicAuthToken = basicAuthToken(userName, password)
        val requestBody = """{"query": "query getToken { getToken { token } }"}"""
        return lobotApiClient.httpPostResponseBody(basicAuthToken, requestBody, BASIC_AUTHORIZATION)
            .removePrefix("""{"data":{"getToken":{"token":"""")
            .removeSuffix(""""}}}""")
    }

    private fun basicAuthToken(userName: String, password: String) : String =
        "${userName}:${password}".toByteArray().let(Base64.getEncoder()::encodeToString)
}
