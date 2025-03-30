package com.supplier_management_service.supplier_management_service.builders

import com.supplier_management_service.supplier_management_service.models.*
import java.time.LocalDate

class SupplierBuilder {
    private var id: String = "123"
    private var supplierName: String = "Test Supplier"
    private var contractType: ContractType = ContractType.NO_CONTRACT
    private var workStatus: WorkStatus = WorkStatus.NOT_APPROVED
    private var requirementsStatus: RequirementStatus = RequirementStatus.PENDING
    private var services: List<String> = emptyList()
    private var states: List<String> = emptyList()
    private var yearsOfOperation: Int = 2
    private var revenue: String = ""
    private var numberOfEmployees: String = ""
    private var contactInfo: ContactInfo = ContactInfo(PrimaryContact("", ""))
    private var businessClassifications: Map<String, Boolean> = emptyMap()
    private var safetyAndCompliance: SafetyAndCompliance = SafetyAndCompliance(
        trir = 2.0,
        emr = 2.0,
        coiUrl = "",
        oshaLogsUrl = "",
        bankInfoUrl = ""
    )
    private var isDiscoverable: Boolean = true
    private var organization: String = ""
    private var onboardingDate: LocalDate = LocalDate.now()

    fun build() = Supplier(
        id = id,
        supplierName = supplierName,
        contractType = contractType,
        workStatus = workStatus,
        requirementsStatus = requirementsStatus,
        services = services,
        states = states,
        yearsOfOperation = yearsOfOperation,
        revenue = revenue,
        numberOfEmployees = numberOfEmployees,
        contactInfo = contactInfo,
        businessClassifications = businessClassifications,
        safetyAndCompliance = safetyAndCompliance,
        isDiscoverable = isDiscoverable,
        organization = organization,
        onboardingDate = onboardingDate
    )

    fun withBusinessClassification(classification: String, value: Boolean): SupplierBuilder {
        val updatedMap = businessClassifications.toMutableMap()
        updatedMap[classification] = value
        businessClassifications = updatedMap
        return this
    }

    fun withBusinessClassifications(classifications: Map<String, Boolean>): SupplierBuilder {
        businessClassifications = classifications
        return this
    }
}