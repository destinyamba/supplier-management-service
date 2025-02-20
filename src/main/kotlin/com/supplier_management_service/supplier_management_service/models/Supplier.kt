package com.supplier_management_service.supplier_management_service.models

import com.supplier_management_service.supplier_management_service.enums.DocumentType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.Embedded

@Document(collection = "suppliers")
data class Supplier(
    @Id
    val id: String? = null,
    val contractType: ContractType? = ContractType.NO_CONTRACT,
    val supplierName: String,
    var workStatus: WorkStatus? = WorkStatus.NOT_APPROVED,
    var requirementsStatus: RequirementStatus? = RequirementStatus.PENDING,
    val services: List<String> = emptyList(),
    val states: List<String> = emptyList(),
    val yearsOfOperation: Int,
    val revenue: String,
    val numberOfEmployees: String,
    val contactInfo: ContactInfo,
    val businessClassifications: Map<String, Boolean>,
    @Embedded
    val safetyAndCompliance: SafetyAndCompliance,
    val isDiscoverable: Boolean = false,
    var organization: String = supplierName,
)

data class ContactInfo(
    val primaryContact: PrimaryContact,
    val secondaryContact: SecondaryContact? = null,
)

@Embeddable
data class SafetyAndCompliance(
    val trir: Double,
    val emr: Double,
    var coiUrl: String? = null,
    var oshaLogsUrl: String? = null,
    var bankInfoUrl: String? = null,
    @ElementCollection
    var submittedDocuments: MutableMap<DocumentType, Boolean> = mutableMapOf()
) {
    fun updateDocumentStatus(documentType: DocumentType, isValid: Boolean) {
        submittedDocuments[documentType] = isValid
    }
}

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
