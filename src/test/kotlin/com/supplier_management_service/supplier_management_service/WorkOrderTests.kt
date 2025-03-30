package com.supplier_management_service.supplier_management_service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.supplier_management_service.supplier_management_service.controllers.WorkOrderController
import com.supplier_management_service.supplier_management_service.dtos.response.WOResponse
import com.supplier_management_service.supplier_management_service.enums.ContractStatus
import com.supplier_management_service.supplier_management_service.enums.Region
import com.supplier_management_service.supplier_management_service.enums.SupplyChainService
import com.supplier_management_service.supplier_management_service.models.WorkOrder
import com.supplier_management_service.supplier_management_service.services.WorkOrderService
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class WorkOrderControllerTests {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var workOrderService: WorkOrderService

    @InjectMocks
    private lateinit var controller: WorkOrderController

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .build()
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `createWorkOrder returns 201 when successful`() {
        val workOrder = WorkOrderBuilder().build()
        val savedOrder = workOrder.copy(id = "wo-123")

        whenever(workOrderService.createWorkOrder(any())).thenReturn(savedOrder)

        mockMvc.perform(
            post("/api/v1/work-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(workOrder))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("wo-123"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `createWorkOrder returns 500 on service failure`() {
        val workOrder = WorkOrderBuilder().build()
        whenever(workOrderService.createWorkOrder(any()))
            .thenThrow(RuntimeException("Database error"))

        mockMvc.perform(
            post("/api/v1/work-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(workOrder))
        )
            .andExpect(status().isInternalServerError)
            .andExpect(content().string(containsString("Error creating work order")))
    }

    @Test
    fun `getWorkOrder returns 200 when found`() {
        val woId = "wo-123"
        val workOrder = WorkOrderBuilder().withId(woId).build()

        whenever(workOrderService.getWorkOrderById(woId)).thenReturn(workOrder)

        mockMvc.perform(get("/api/v1/work-order/$woId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(woId))
    }

    @Test
    fun `getWorkOrder returns 500 when not found`() {
        val invalidId = "invalid-123"
        whenever(workOrderService.getWorkOrderById(invalidId))
            .thenThrow(RuntimeException("Not found"))

        mockMvc.perform(get("/api/v1/work-order/$invalidId"))
            .andExpect(status().isInternalServerError)
    }

    @Test
    @WithMockUser(roles = ["EDITOR"])
    fun `cancelWO returns 204 when successful`() {
        val clientId = "client-123"
        val woId = "wo-456"
        doNothing().whenever(workOrderService).voidWO(clientId, woId)

        mockMvc.perform(delete("/api/v1/work-order/delete/$clientId/$woId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `cancelWO returns 404 when not found`() {
        val invalidClientId = "invalid-client"
        val woId = "wo-456"
        doThrow(IllegalArgumentException("Not found"))
            .whenever(workOrderService).voidWO(invalidClientId, woId)

        mockMvc.perform(delete("/api/v1/work-order/delete/$invalidClientId/$woId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getRegions returns all when no country filter`() {
        val regions = listOf(
            Region(
                "Belfast", "BT",
                country = "UK"
            ),
            Region(
                "Bedfordshire", "BD",
                country = "UK"
            )
        )
        whenever(workOrderService.getAllRegions()).thenReturn(regions)

        mockMvc.perform(get("/api/v1/work-order/locations"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `getServices returns all when no category filter`() {
        val services = listOf(
            SupplyChainService("Electrical", "Installation"),
            SupplyChainService("Plumbing", "Repair")
        )
        whenever(workOrderService.getServices()).thenReturn(services)

        mockMvc.perform(get("/api/v1/work-order/services"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `endpoints allow CORS from configured origin`() {
        mockMvc.perform(
            post("/api/v1/work-order")
                .header("Origin", "http://localhost:3000")
        )
            .andExpect(header().exists("Access-Control-Allow-Origin"))
    }
}

class WorkOrderBuilder {
    private var id: String? = null
    private var clientId: String = "client-123"

    fun withId(id: String) = apply { this.id = id }
    fun build() = WorkOrder(
        id = "wo-123",
        status = ContractStatus.PENDING,
        clientId = clientId,
        location = "Belfast",
        dueDate = Date(),
        startDate = Date(),
        projectManager = "project manager",
        workOrderNumber = "",
        taskDescription = "",
        service = ""
    )
}

class WOResponseBuilder {
    fun build() = WOResponse(
        wo = WorkOrder(
            id = "wo-123",
            status = ContractStatus.PENDING,
            clientId = "",
            location = "Belfast",
            dueDate = Date(),
            startDate = Date(),
            projectManager = "project manager",
            workOrderNumber = "wo-123",
            taskDescription = "",
            service = ""
        )
    )
}

data class RegionBuilder(
    var name: String = "North America",
    var country: String = "US"
) {
    fun build() = Region(
        name = name,
        country = country,
        abbreviation = ""
    )
}

data class SupplyChainServiceBuilder(
    var category: String = "Electrical",
    var serviceName: String = "Installation"
) {
    fun build() = SupplyChainService(category, serviceName)
}