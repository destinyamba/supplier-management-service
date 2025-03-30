package com.supplier_management_service.supplier_management_service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.supplier_management_service.supplier_management_service.builders.ClientsDTOBuilder
import com.supplier_management_service.supplier_management_service.controllers.ClientController
import com.supplier_management_service.supplier_management_service.models.*
import com.supplier_management_service.supplier_management_service.services.ClientService
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.security.test.context.support.WithMockUser
import java.util.*

@ExtendWith(MockitoExtension::class)
class ClientTests {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var clientService: ClientService

    @InjectMocks
    private lateinit var clientController: ClientController

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build()
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should onboard client successfully`() {
        val clientDTO = ClientsDTOBuilder()
            .id(UUID.randomUUID().toString())
            .clientName("Test Client")
            .contactInfo(ContactInfo(PrimaryContact("test@email.com", "test")))
            .yearsOfOperation(5)
            .build()
        whenever(clientService.onboardClient(any())).thenReturn(clientDTO)

        val response = mockMvc.perform(
            post("/api/v1/client/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientDTO))
        )
        response.andDo { print(it.response.contentAsString) }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should return 500 if onboarding fails`() {
        val clientDTO = ClientsDTOBuilder()
            .id(UUID.randomUUID().toString())
            .clientName("Test Client")
            .contactInfo(ContactInfo(PrimaryContact("test@email.com", "test")))
            .yearsOfOperation(5)
            .build()
        whenever(clientService.onboardClient(any())).doThrow(RuntimeException("Service error"))

        mockMvc.perform(
            post("/api/v1/client/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientDTO))
        )
            .andExpect(status().isInternalServerError)
    }

    @Test
    @WithMockUser(roles = ["EDITOR"])
    fun `onboard client returns 200 for valid request`() {
        val clientDTO = ClientsDTOBuilder().validClient().build()
        whenever(clientService.onboardClient(any())).thenReturn(clientDTO)

        mockMvc.perform(
            post("/api/v1/client/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientDTO))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.clientName").value(clientDTO.clientName))
    }

}



