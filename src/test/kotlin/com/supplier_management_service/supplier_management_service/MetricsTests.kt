package com.supplier_management_service.supplier_management_service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.supplier_management_service.supplier_management_service.controllers.MetricsController
import com.supplier_management_service.supplier_management_service.dtos.response.ServiceTypeMetric
import com.supplier_management_service.supplier_management_service.dtos.response.WorkStatusMetric
import com.supplier_management_service.supplier_management_service.services.MetricsService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class MetricsTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var metricsService: MetricsService

    private val objectMapper = jacksonObjectMapper()

    @InjectMocks
    private lateinit var metricsController: MetricsController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(metricsController)
            .setMessageConverters(MappingJackson2HttpMessageConverter())
            .build()
    }

    @Test
    fun `should return total suppliers for a given client`() {
        val clientId = UUID.randomUUID().toString()
        val totalSuppliers = 100

        Mockito.`when`(metricsService.clientTotalSuppliers(clientId)).thenReturn(totalSuppliers)

        mockMvc.perform(
            get("/api/v1/metrics/total-suppliers")
                .param("clientId", clientId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json("$totalSuppliers"))
    }

    @Test
    fun `should return total work orders for a given client`() {
        val clientId = "12345"
        val totalWorkOrders = 200

        Mockito.`when`(metricsService.clientTotalWorkOrders(clientId)).thenReturn(totalWorkOrders)

        mockMvc.perform(
            get("/api/v1/metrics/total-work-orders")
                .param("clientId", clientId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json("$totalWorkOrders"))
    }

    @Test
    fun `should return error when total work orders fail`() {
        val clientId = "12345"

        Mockito.`when`(metricsService.clientTotalWorkOrders(clientId)).thenThrow(RuntimeException("Service error"))

        mockMvc.perform(
            get("/api/v1/metrics/total-work-orders")
                .param("clientId", clientId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `should return upcoming work orders for a given client`() {
        val clientId = "12345"
        val upcomingWorkOrders = 50

        Mockito.`when`(metricsService.getUpcomingWorkOrders(clientId)).thenReturn(upcomingWorkOrders)

        mockMvc.perform(
            get("/api/v1/metrics/upcoming-work-orders")
                .param("clientId", clientId)
                .accept(MediaType.APPLICATION_JSON)
        )

            .andExpect(status().isOk)
            .andExpect(content().json("$upcomingWorkOrders"))
    }

    @Test
    fun `endpoints allow CORS from configured origin`() {
        mockMvc.perform(
            get("/api/v1/metrics/total-suppliers")
                .header("Origin", "http://localhost:3000")
                .param("clientId", UUID.randomUUID().toString())
        )
            .andExpect(header().exists("Access-Control-Allow-Origin"))
    }

    @Test
    fun `endpoints allow unauthenticated access`() {
        mockMvc.perform(
            get("/api/v1/metrics/total-suppliers")
                .param("clientId", UUID.randomUUID().toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `get suppliers by service successfully`() {
        val clientId = UUID.randomUUID().toString()
        val testServiceMetrics = listOf(
            ServiceTypeMetric("Electrical", 15),
            ServiceTypeMetric("HVAC", 8)
        )

        given(metricsService.getClientWorkOrdersByService(clientId))
            .willReturn(testServiceMetrics)

        mockMvc.perform(
            get("/api/v1/metrics/suppliers-by-service")
                .param("clientId", clientId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.size()").value(2)) // Correct size check
            .andExpect(jsonPath("$[1].serviceType").value("HVAC"))
            .andExpect(jsonPath("$[1].count").value(8))
    }

    @Test
    fun `get work orders by service successfully`() {
        val clientId = UUID.randomUUID().toString()
        val testServiceMetrics = listOf(
            ServiceTypeMetric("Electrical", 15),
            ServiceTypeMetric("HVAC", 8)
        )
        given(metricsService.getClientWorkOrdersByService(clientId))
            .willReturn(testServiceMetrics)

        mockMvc.perform(
            get("/api/v1/metrics/work-order-service")
                .param("clientId", clientId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].serviceType").value("Electrical"))
            .andExpect(jsonPath("$[0].count").value(15))
    }

    @Test
    fun `get work status count successfully`() {
        val clientId = UUID.randomUUID().toString()
        val testWorkStatusMetric = WorkStatusMetric(
            notApproved = 2,
            approved = 15,
        )
        given(metricsService.getClientSuppliersWorkStatusCount(clientId))
            .willReturn(testWorkStatusMetric)

        mockMvc.perform(
            get("/api/v1/metrics/work-status")
                .param("clientId", clientId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.notApproved").value(2))
            .andExpect(jsonPath("$.approved").value(15))
    }
}
