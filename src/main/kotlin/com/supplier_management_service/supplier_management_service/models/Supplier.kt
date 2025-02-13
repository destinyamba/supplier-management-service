package com.supplier_management_service.supplier_management_service.models

import com.nimbusds.openid.connect.sdk.assurance.evidences.Organization
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
    val businessClassifications: Map<String, Boolean>,
    val safetyAndCompliance: SafetyAndCompliance,
    val isDiscoverable: Boolean = false,
    var organization: String = supplierName,
)

data class ContactInfo(
    val primaryContact: PrimaryContact
)

data class SafetyAndCompliance(
    val trir: Double,
    val emr: Double,
    var coiUrl: String? = null,
    var safetyProgramUrl: String? = null,
    var oshaLogsUrl: String? = null,
    var bankInfoUrl: String? = null,
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
