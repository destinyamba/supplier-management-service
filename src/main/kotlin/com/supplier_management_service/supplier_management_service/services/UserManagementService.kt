package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.models.Auth0User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class UserManagementService(
    private val auth0TokenService: Auth0TokenService,
    private val webClientBuilder: WebClient.Builder,
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val issuerUri: String,
) {

    private val webClient = webClientBuilder
        .baseUrl("https://dev-wq7kgbrb43seerv2.us.auth0.com/api/v2")
        .build()


    fun getUsers(): List<Auth0User> {
        val token = auth0TokenService.getToken()

        return webClient.get()
            .uri("/users")
            .header("authorization", "Bearer $token")
            .header("Accept", "application/json")
            .retrieve()
            .bodyToMono(Array<Auth0User>::class.java)
            .map { it.toList() }
            .block() ?: emptyList()
    }

    fun getUser(): String {
        val text = "This is a public message."

        return text
    }

    fun getProtectedUser(): String {
        val text = "This is a privates message."

        return text
    }

    fun getAdminUser(): String {
        val text = "This is an admin message."

        return text
    }
}
