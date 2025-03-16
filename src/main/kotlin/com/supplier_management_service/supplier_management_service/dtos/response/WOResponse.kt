package com.supplier_management_service.supplier_management_service.dtos.response

import com.supplier_management_service.supplier_management_service.enums.ContractStatus
import com.supplier_management_service.supplier_management_service.models.WorkOrder
import java.util.*

data class WOResponse(
    val id: String? = null,
    val status: ContractStatus = ContractStatus.PENDING,
    val clientId: String,
    val location: String,
    val dueDate: Date,
    val startDate: Date,
    val supplierIds: List<String> = emptyList(),
    val projectManager: String,
    val workOrderNumber: String? = null,
    val taskDescription: String,
    val service: String,
) {
    constructor(wo: WorkOrder) : this(
        id = wo.id.toString(),
        status = wo.status,
        clientId = wo.clientId,
        location = wo.location,
        dueDate = wo.dueDate,
        startDate = wo.startDate,
        supplierIds = wo.supplierIds,
        projectManager = wo.projectManager!!,
        workOrderNumber = wo.workOrderNumber,
        taskDescription = wo.taskDescription,
        service = wo.service
    )
}


data class WOPagedResponse<T>(
    val wos: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)