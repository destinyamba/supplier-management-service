package com.supplier_management_service.supplier_management_service.services

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.Instant
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap


@Service
class Auth0TokenService(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${spring.security.oauth2.resourceserver.jwt.client-id}") private val clientId: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.client-secret}") private val clientSecret: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val issuerUri: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.audience}") private val audience: String
) {

    private val sanitizedIssuerUri = if (issuerUri.startsWith("http")) issuerUri else "https://$issuerUri"

    private val webClient = webClientBuilder
        .baseUrl(sanitizedIssuerUri)
        .build()

    private var token: String? = null
    private var tokenExpiry: Instant = Instant.MIN

    fun getToken(): String {
        if (token == null || Instant.now().isAfter(tokenExpiry)) {
            refreshToken()
        }
        return token ?: throw IllegalStateException("Token could not be retrieved")
    }

    private fun refreshToken() {
        try {
            val formData = mapOf(
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "audience" to audience,
                "grant_type" to "client_credentials",
            )

            val response = webClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(
                    BodyInserters.fromFormData(
                        formData.entries.fold(LinkedMultiValueMap()) { map, entry ->
                            map.add(entry.key, entry.value)
                            map
                        }
                    ))
                .retrieve()
                .bodyToMono(JsonNode::class.java)
                .block() ?: throw Exception("Failed to fetch token: null response")

            token = response["access_token"].asText()
            val expiresIn = response["expires_in"].asLong()
            tokenExpiry = Instant.now().plusSeconds(expiresIn - 60)
        } catch (e: Exception) {
            throw Exception("Failed to fetch token: ${e.message}", e)
        }
    }

}