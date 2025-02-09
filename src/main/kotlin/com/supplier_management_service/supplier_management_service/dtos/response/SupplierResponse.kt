package com.supplier_management_service.supplier_management_service.dtos.response

import com.supplier_management_service.supplier_management_service.models.*

data class SupplierResponse(
    val id: String? = null,
    val contractType: ContractType? = ContractType.NO_CONTRACT,
    val supplierName: String,
    val workStatus: WorkStatus? = WorkStatus.NOT_APPROVED,
    val requirementsStatus: RequirementStatus? = RequirementStatus.PENDING,
    val services: List<String> = emptyList(),
    val states: List<String> = emptyList(),
    val yearsOfOperation: Int,
    val revenue: String,
    val numberOfEmployees: String,
    val contactInfo: ContactInfo,
    val businessClassifications: List<String> = emptyList(),
    val safetyAndCompliance: SafetyAndCompliance,
) {
    constructor(patient: Supplier) : this(
        id = patient.id.toString(),
        contractType = patient.contractType,
        supplierName = patient.supplierName,
        workStatus = patient.workStatus,
        requirementsStatus = patient.requirementsStatus,
        services = patient.services,
        states = patient.states,
        yearsOfOperation = patient.yearsOfOperation,
        revenue = patient.revenue,
        numberOfEmployees = patient.numberOfEmployees,
        contactInfo = patient.contactInfo,
        businessClassifications = patient.businessClassifications,
        safetyAndCompliance = patient.safetyAndCompliance,
    )
}

data class PagedResponse<T>(
    val suppliers: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)