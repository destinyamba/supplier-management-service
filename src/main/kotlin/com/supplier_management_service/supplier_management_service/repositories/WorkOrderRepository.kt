package com.supplier_management_service.supplier_management_service.repositories

import com.supplier_management_service.supplier_management_service.models.User
import com.supplier_management_service.supplier_management_service.models.WorkOrder
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface WorkOrderRepository : MongoRepository<WorkOrder, String?> {
    fun findByClientId(clientId: String): List<WorkOrder>
    fun findByServiceIn(services: Set<String>): List<WorkOrder>
    fun findByClientIdAndDueDateBetween(clientId: String, startDate: LocalDate, endDate: LocalDate): List<WorkOrder>

}
