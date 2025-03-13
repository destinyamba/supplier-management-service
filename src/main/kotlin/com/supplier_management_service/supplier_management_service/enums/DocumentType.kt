package com.supplier_management_service.supplier_management_service.enums

enum class DocumentType(val keyword: String) {
    COI("Insurance"),
    BANK_INFO("Bank"),
    OSHA_LOG("OSHA")
}

data class DocumentValidationResult(
    val documentType: DocumentType?,
    val isValid: Boolean,
    val reason: String
)