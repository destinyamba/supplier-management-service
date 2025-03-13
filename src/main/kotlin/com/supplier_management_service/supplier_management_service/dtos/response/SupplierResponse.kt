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
    constructor(supplier: Supplier) : this(
        id = supplier.id.toString(),
        contractType = supplier.contractType,
        supplierName = supplier.supplierName,
        workStatus = supplier.workStatus,
        requirementsStatus = supplier.requirementsStatus,
        services = supplier.services,
        states = supplier.states,
        yearsOfOperation = supplier.yearsOfOperation,
        revenue = supplier.revenue,
        numberOfEmployees = supplier.numberOfEmployees,
        contactInfo = supplier.contactInfo,
        businessClassifications = supplier.businessClassifications.filterValues { it }.keys.toList(),
        safetyAndCompliance = supplier.safetyAndCompliance,
    )
}

data class PagedResponse<T>(
    val suppliers: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)