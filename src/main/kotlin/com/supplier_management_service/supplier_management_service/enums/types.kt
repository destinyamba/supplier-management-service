package com.supplier_management_service.supplier_management_service.enums

data class Region(
    val name: String,
    val abbreviation: String,
    val country: String
)

data class SupplyChainService(
    val name: String,
    val category: String
)

enum class ContractStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

