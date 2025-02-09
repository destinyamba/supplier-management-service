package com.supplier_management_service.supplier_management_service.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "suppliers")
data class Supplier(
    @Id
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
)

data class ContactInfo(
    val primaryContact: PrimaryContact
)

data class SafetyAndCompliance(
    val trir: Double,
    val emr: Double,
    val coi: String,
    val safetyProgram: String,
    val oshaLogs: String,
    val bankInfo: String
)

enum class WorkStatus {
    APPROVED,
    NOT_APPROVED,
}

enum class ContractType {
    NO_CONTRACT,
    DIRECT,
    SUBCONTRACTED
}

enum class RequirementStatus {
    PENDING,
    SUBMITTED,
}
