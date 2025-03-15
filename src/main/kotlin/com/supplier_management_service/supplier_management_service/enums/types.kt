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

val supplyChainServices = listOf(
    SupplyChainService("Warehousing", "Storage"),
    SupplyChainService("Inventory Management", "Storage"),
    SupplyChainService("Freight Forwarding", "Transportation"),
    SupplyChainService("Customs Clearance", "Compliance"),
    SupplyChainService("Procurement", "Sourcing"),
    SupplyChainService("Supplier Auditing", "Compliance"),
    SupplyChainService("Packaging and Labeling", "Processing"),
    SupplyChainService("Last-Mile Delivery", "Transportation"),
    SupplyChainService("Reverse Logistics", "Returns"),
    SupplyChainService("Demand Planning", "Planning"),
    SupplyChainService("Route Optimization", "Technology"),
    SupplyChainService("Cold Chain Logistics", "Specialized"),
    SupplyChainService("E-Commerce Fulfillment", "Fulfillment"),
    SupplyChainService("Drop Shipping", "Fulfillment"),
    SupplyChainService("Quality Assurance", "Inspection"),
    SupplyChainService("Supply Chain Analytics", "Technology"),
    SupplyChainService("Sustainability Consulting", "Consulting"),
    SupplyChainService("Fleet Management", "Transportation"),
    SupplyChainService("Custom Product Assembly", "Processing"),
    SupplyChainService("Integrated Logistics Solutions", "Comprehensive")
)

enum class ContractStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

