package com.supplier_management_service.supplier_management_service.models

import com.supplier_management_service.supplier_management_service.config.customannotation.AutoGenerateWorkOrderNumber
import com.supplier_management_service.supplier_management_service.enums.ContractStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("workOrders")
data class WorkOrder(
    @Id
    val id: String? = null,
    var status: ContractStatus = ContractStatus.PENDING,
    val clientId: String,
    val location: String,
    val dueDate: Date,
    val startDate: Date,
    @Indexed
    val supplierIds: List<String> = emptyList(),
    val projectManager: String? = null,
    @Indexed(unique = true)
    @AutoGenerateWorkOrderNumber
    var workOrderNumber: String? = null,
    val taskDescription: String,
    val service: String,
)

