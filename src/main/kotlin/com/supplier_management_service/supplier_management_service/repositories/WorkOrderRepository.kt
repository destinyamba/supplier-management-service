package com.supplier_management_service.supplier_management_service.repositories

import com.supplier_management_service.supplier_management_service.models.User
import com.supplier_management_service.supplier_management_service.models.WorkOrder
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkOrderRepository : MongoRepository<WorkOrder, String?> {
    fun findByClientId(clientId: String): List<WorkOrder>
}
