package com.supplier_management_service.supplier_management_service.services

import com.fasterxml.jackson.annotation.JsonProperty
import com.supplier_management_service.supplier_management_service.models.Auth0User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class UserManagementService(
    private val auth0TokenService: Auth0TokenService,
    webClientBuilder: WebClient.Builder,
    @Value("\${spring.security.oauth2.resourceserver.jwt.audience}") private val audience: String,
) {

    private val webClient = webClientBuilder
        .baseUrl(audience)
        .build()

    fun getUsers(page: Int = 0, perPage: Int = 10): PaginatedResponse<Auth0User> {
        val token = auth0TokenService.getToken()

        val response = webClient.get()
            .uri { builder ->
                builder.path("/users")
                    .queryParam("page", page)
                    .queryParam("per_page", perPage)
                    .queryParam("include_totals", true)
                    .build()
            }
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .retrieve()
            .bodyToMono(Auth0PaginatedResponse::class.java)
            .block() ?: throw Exception("Failed to fetch users")


        return PaginatedResponse(
            totalUsers = response.totalUsers,
            page = page,
            usersPerPage = perPage,
            users = response.users
        )
    }
}

data class PaginatedResponse<T>(
    val totalUsers: Int,
    val page: Int,
    val usersPerPage: Int,
    val users: List<T>
)

data class Auth0PaginatedResponse(
    @JsonProperty("total") val totalUsers: Int,
    @JsonProperty("start") val startIndex: Int,
    @JsonProperty("limit") val limit: Int,
    @JsonProperty("users") val users: List<Auth0User>
)

