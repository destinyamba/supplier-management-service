package com.supplier_management_service.supplier_management_service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.supplier_management_service.supplier_management_service.controllers.UserManagementController
import com.supplier_management_service.supplier_management_service.dtos.request.EditUserRequest
import com.supplier_management_service.supplier_management_service.models.*
import com.supplier_management_service.supplier_management_service.services.UserManagementService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(MockitoExtension::class)
class UserManagementControllerTests {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var userManagementService: UserManagementService

    @InjectMocks
    private lateinit var controller: UserManagementController

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        objectMapper.registerModule(JavaTimeModule())
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .build()
    }

    @Test
    fun `getUserDetails by email returns user when exists`() {
        val email = "user@example.com"
        val userDetails = UserDetailsBuilder().build()

        whenever(userManagementService.getUserDetails(email)).thenReturn(userDetails)

        mockMvc.perform(
            post("/api/v1/user-details/get-user-details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UserRequest(email)))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(email))
    }

    @Test
    fun `getUserDetails returns 404 when email not found`() {
        val email = "nonexistent@example.com"
        whenever(userManagementService.getUserDetails(email))
            .thenThrow(IllegalArgumentException("User not found"))

        mockMvc.perform(
            post("/api/v1/user-details/get-user-details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UserRequest(email)))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getAssociatedOrgUsers returns users when org exists`() {
        val orgId = "org-123"
        val users = listOf(
            UserDetailsBuilder().withOrg(orgId).build(),
            UserDetailsBuilder().withOrg(orgId).build()
        )

        whenever(userManagementService.usersUnderOrg(orgId)).thenReturn(users)

        mockMvc.perform(get("/api/v1/user-details/associated-org-users/$orgId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `getAssociatedOrgUsers returns 404 when org not found`() {
        val invalidOrgId = "invalid-org"
        whenever(userManagementService.usersUnderOrg(invalidOrgId))
            .thenThrow(IllegalArgumentException("Organization not found"))

        mockMvc.perform(get("/api/v1/user-details/associated-org-users/$invalidOrgId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `inviteUserInitial returns created user`() {
        val pendingUser = PendingUserBuilder().build()
        val createdUser = PendingUserBuilder().withId("pending@example.com").build()

        whenever(userManagementService.inviteUserInitial(pendingUser)).thenReturn(createdUser)

        mockMvc.perform(
            post("/api/v1/user-details/invite-user-initial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pendingUser))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("pending@example.com"))
    }

    @Test
    fun `inviteUserComplete activates user successfully`() {
        val email = "user@example.com"
        val password = "securePassword123!"
        val userDetails = UserDetailsBuilder().withEmail(email).build()

        whenever(userManagementService.inviteUserComplete(email, password)).thenReturn(userDetails)

        mockMvc.perform(
            post("/api/v1/user-details/invite-user-complete/$email/$password")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(email))
    }

    @Test
    fun `deleteUser returns 204 when successful`() {
        val userId = "user-123"
        doNothing().whenever(userManagementService).deleteUser(userId)

        mockMvc.perform(delete("/api/v1/user-details/delete/user/$userId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteUser returns 404 when user not found`() {
        val invalidUserId = "invalid-user"
        doThrow(IllegalArgumentException("User not found"))
            .whenever(userManagementService).deleteUser(invalidUserId)

        mockMvc.perform(delete("/api/v1/user-details/delete/user/$invalidUserId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `editUser updates successfully`() {
        val editRequest = EditUserRequestBuilder().build()
        doNothing().whenever(userManagementService).editUser(editRequest)

        mockMvc.perform(
            put("/api/v1/user-details/update/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editRequest))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `all endpoints return CORS headers`() {
        val endpoints = listOf(
            post("/api/v1/user-details/get-user-details"),
            get("/api/v1/user-details/associated-org-users/org123"),
            post("/api/v1/user-details/invite-user-initial")
        )

        endpoints.forEach { endpoint ->
            mockMvc.perform(
                endpoint
                    .header("Origin", "http://localhost:3000")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(header().exists("Access-Control-Allow-Origin"))
        }
    }

}

class UserDetailsBuilder {
    private var id: String = UUID.randomUUID().toString()
    private var email: String = "user@example.com"
    private var orgId: String = "org-123"

    fun withId(id: String) = apply { this.id = id }
    fun withEmail(email: String) = apply { this.email = email }
    fun withOrg(orgId: String) = apply { this.orgId = orgId }
    fun build() = UserDetails(
        id, email, orgId, Role.ADMIN, "Name",
        organizationName = "organisation name",
        lastSignIn = LocalDateTime.now(),
        createdAt = LocalDateTime.now(),
        orgId = "Org Id",
    )
}

class PendingUserBuilder {
    private var email: String = "pending@example.com"
    private var orgId: String = "org-123"

    fun withEmail(email: String) = apply { this.email = email }
    fun withId(id: String) = apply { this.orgId = id }
    fun build() = PendingUser(
        email, orgId, "name", Role.EDITOR,
        orgId = "orgId",
        orgName = "orgName",
        businessType = BusinessType.CLIENT,
        createdAt = LocalDateTime.now(),
        lastSignIn = LocalDateTime.now(),
    )
}

class EditUserRequestBuilder {
    private var userId: String = UUID.randomUUID().toString()
    private var roles: List<String> = listOf("ROLE_EDITOR")

    fun build() = EditUserRequest(
        userId = "",
        newName = "",
        newEmail = "",
        newRole = Role.EDITOR
    )
}