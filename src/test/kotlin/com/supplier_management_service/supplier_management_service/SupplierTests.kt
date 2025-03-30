package com.supplier_management_service.supplier_management_service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.supplier_management_service.supplier_management_service.builders.SupplierBuilder
import com.supplier_management_service.supplier_management_service.controllers.SupplierController
import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.*
import com.supplier_management_service.supplier_management_service.services.SupplierOnboardingService
import com.supplier_management_service.supplier_management_service.services.SupplierSearchService
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockPart
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(MockitoExtension::class)
class SupplierTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var supplierOnboardingService: SupplierOnboardingService

    @Mock
    private lateinit var supplierSearchService: SupplierSearchService

    private val objectMapper = jacksonObjectMapper()

    @InjectMocks
    private lateinit var supplierController: SupplierController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(supplierController).build()
    }

    @Test
    fun `should build Supplier with default values`() {
        // When
        val supplier = SupplierBuilder().build()

        // Then
        assertEquals("123", supplier.id)
        assertEquals("Test Supplier", supplier.supplierName)
        assertEquals(ContractType.NO_CONTRACT, supplier.contractType)
        assertEquals(WorkStatus.NOT_APPROVED, supplier.workStatus)
        assertEquals(RequirementStatus.PENDING, supplier.requirementsStatus)
        assertTrue(supplier.services.isEmpty())
        assertTrue(supplier.states.isEmpty())
        assertEquals(2, supplier.yearsOfOperation)
        assertEquals("", supplier.revenue)
        assertEquals("", supplier.numberOfEmployees)
        assertEquals(emptyMap<String, Boolean>(), supplier.businessClassifications)
        assertTrue { (supplier.isDiscoverable) }
        assertEquals("", supplier.organization)
        assertNotNull(supplier.onboardingDate)
    }

    @Test
    fun `should build Supplier with custom business classifications`() {
        // When
        val supplier = SupplierBuilder()
            .withBusinessClassification("VeteranOwned", true)
            .withBusinessClassification("WomanOwned", false)
            .build()

        // Then
        assertEquals(mapOf("VeteranOwned" to true, "WomanOwned" to false), supplier.businessClassifications)
    }

    @Test
    fun `should build Supplier with all business classifications set at once`() {
        // Given
        val classifications = mapOf(
            "VeteranOwned" to true,
            "WomanOwned" to true,
            "MinorityOwned" to false
        )

        // When
        val supplier = SupplierBuilder()
            .withBusinessClassifications(classifications)
            .build()

        // Then
        assertEquals(classifications, supplier.businessClassifications)
    }

    @Test
    fun `should search suppliers with NLP successfully`() {
        // Given
        val query = "construction services"
        val page = 1
        val pageSize = 10
        val testSupplierResponse = SupplierResponse(
            supplier = Supplier(
                supplierName = "supplier name",
                yearsOfOperation = 2,
                revenue = "",
                numberOfEmployees = "",
                contactInfo = ContactInfo(PrimaryContact("email", "name")),
                businessClassifications = mapOf(IBusinessClassification.SmallBusiness.name to true, IBusinessClassification.WomanOwned.name to true),
                safetyAndCompliance = SafetyAndCompliance(
                    trir = 2.0,
                    emr = 2.0,
                    coiUrl = "",
                    oshaLogsUrl = "",
                    bankInfoUrl = "",

                    ),
                onboardingDate = LocalDate.now()
            )
        )
        val suppliersResponse = PagedResponse(
            suppliers = listOf(testSupplierResponse),
            page = page,
            pageSize = pageSize,
            totalPages = 1,
            totalItems = 1
        )

        `when`(supplierSearchService.nlpSearchSuppliers(query, page, pageSize))
            .thenReturn(suppliersResponse)

        // When
        val response = supplierController.searchSuppliers(query, page, pageSize)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(suppliersResponse, response.body)
    }

    @Test
    fun `onboardSupplier returns 400 when bad request`() {
        mockMvc.perform(multipart("/api/v1/supplier/onboard-supplier"))
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `onboardSupplier handles large files appropriately`() {
        val largeFile = MockMultipartFile(
            "coi",
            "large.pdf",
            "application/pdf",
            ByteArray(10_000_000)  // 10MB file
        )

        mockMvc.perform(
            multipart("/api/v1/supplier/onboard-supplier")
                .file(largeFile)
                .part(MockPart("supplierData", "{}".toByteArray()))
        )
            .andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `endpoints allow CORS from configured origin`() {
        mockMvc.perform(
            get("/api/v1/supplier/all")
                .header("Origin", "http://localhost:3000")
        )
            .andExpect(header().exists("Access-Control-Allow-Origin"))
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `endpoints block CORS from unconfigured origins`() {
        mockMvc.perform(
            get("/api/v1/supplier/all")
                .header("Origin", "http://malicious-site.com")
        )
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"))
    }
}
