package com.expedia.ccat5563.client

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private const val API_ENDPOINT = "https://lobot-api-java.us-east-1.prod.expedia.com/highlander"

class LobotApiClient {
    fun httpPostResponseBody(authToken: String, requestBody: String, isAuthorizationBasic: Boolean = false) : String {
        val response = HttpClient.newHttpClient().send(
            HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .uri(URI.create(API_ENDPOINT))
            .header("Authorization", "${if (isAuthorizationBasic) "Basic" else "Bearer"} $authToken")
            .header("Content-Type", "application/json")
            .build(),
            HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}
