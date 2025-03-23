package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.dtos.response.*
import com.supplier_management_service.supplier_management_service.enums.ContractStatus
import com.supplier_management_service.supplier_management_service.models.ContractType
import com.supplier_management_service.supplier_management_service.models.RequirementStatus
import com.supplier_management_service.supplier_management_service.models.WorkStatus
import com.supplier_management_service.supplier_management_service.repositories.ClientRepository
import com.supplier_management_service.supplier_management_service.repositories.WorkOrderRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Service
class MetricsService(private val clientRepository: ClientRepository, private val workOrderRepository: WorkOrderRepository) {
    private val logger: Logger = LoggerFactory.getLogger(MetricsService::class.java)

    // get total number of client suppliers.
    fun clientTotalSuppliers(clientId: String): Int {
        val client = clientRepository.findClientById(clientId)
        var totalNumberOfSuppliers = 0
        if (client != null) {
            totalNumberOfSuppliers = client.suppliers?.size!!
        }
        return totalNumberOfSuppliers
    }

    // get total number of client work orders.
    fun clientTotalWorkOrders(clientId: String): Int {
        val totalWO = workOrderRepository.findByClientId(clientId).size
        return totalWO
    }

    // get upcoming work orders; time frame = 30 days
    fun getUpcomingWorkOrders(clientId: String): Int {
        val currentDate = LocalDate.now()
        val endDate = currentDate.plusDays(30)
        val totalUpcomingWO = workOrderRepository.findByClientIdAndDueDateBetween(clientId, currentDate, endDate).size
        return totalUpcomingWO
    }

    // get suppliers requirement status count; i.e. RequirementStatus.Pending = 4, Requirements.Submitted = 9
    fun getClientSuppliersRequirementStatusCount(clientId: String): RequirementStatusMetric {
        val client = clientRepository.findClientById(clientId)
        var pendingStatusCount = 0
        var submittedStatusCount = 0
        if (client != null) {
            // find all suppliers that have pending || submitted requirement status.
            client.suppliers?.forEach { supplier ->
                when (supplier.requirementsStatus) {
                    RequirementStatus.PENDING -> pendingStatusCount++
                    RequirementStatus.SUBMITTED -> submittedStatusCount++
                    else -> {}
                }
            }
        }
        return RequirementStatusMetric(pending = pendingStatusCount, submitted = submittedStatusCount)
    }

    // get suppliers work status count
    fun getClientSuppliersWorkStatusCount(clientId: String): WorkStatusMetric {
        val client = clientRepository.findClientById(clientId)
        var approvedStatusCount = 0
        var notApprovedStatusCount = 0
        if (client != null) {
            // find all suppliers that have pending || submitted requirement status.
            client.suppliers?.forEach { supplier ->
                when (supplier.workStatus) {
                    WorkStatus.APPROVED -> approvedStatusCount++
                    WorkStatus.NOT_APPROVED -> notApprovedStatusCount++
                    else -> {}
                }
            }
        }
        return WorkStatusMetric(approved = approvedStatusCount, notApproved = notApprovedStatusCount)
    }

    // get suppliers contract type count
    fun getClientSuppliersContractTypeCount(clientId: String): ContractTypeMetric {
        val client = clientRepository.findClientById(clientId)
        var directCount = 0
        var subcontractedCount = 0
        if (client != null) {
            // find all suppliers that have pending || submitted requirement status.
            client.suppliers?.forEach { supplier ->
                when (supplier.contractType) {
                    ContractType.DIRECT -> directCount++
                    ContractType.SUBCONTRACTED -> subcontractedCount++
                    else -> {}
                }
            }
        }
        return ContractTypeMetric(direct = directCount, subcontracted = subcontractedCount)
    }

    // get work orders by status; i.e.
    fun getClientWOStatusCount(clientId: String): WorkOrderStatusMetric {
        val client = clientRepository.findClientById(clientId)
        val wos = workOrderRepository.findByClientId(clientId)
        var pendingCount = 0
        var inProgressCount = 0
        var completedCount = 0
        var cancelledCount = 0
        if (client != null) {
            wos.forEach { wo ->
                when (wo.status) {
                    ContractStatus.PENDING -> pendingCount++
                    ContractStatus.IN_PROGRESS -> inProgressCount++
                    ContractStatus.COMPLETED -> completedCount++
                    ContractStatus.CANCELLED -> cancelledCount++
                    else -> {}
                }
            }
        }
        return WorkOrderStatusMetric(
            pending = pendingCount,
            inProgress = inProgressCount,
            completed = completedCount,
            cancelled = cancelledCount
        )
    }

    // get suppliers a client has onboarded over time
    fun getSuppliersOnboardedOverTime(clientId: String): List<OnboardingTrend> {
        val client = clientRepository.findClientById(clientId)
        val onboardingTrends = mutableMapOf<LocalDate, Int>()

        if (client != null) {
            client.suppliers?.forEach { supplier ->
                val onboardingDate = supplier.onboardingDate
                if (onboardingDate != null) {
                    onboardingTrends[onboardingDate] = onboardingTrends.getOrDefault(onboardingDate, 0) + 1
                }
            }
        }

        return onboardingTrends.map { OnboardingTrend(it.key, it.value) }.sortedBy { it.date }
    }

    // get average time to complete clients work orders
    fun getWOAverageCompletion(clientId: String): List<AverageCompletionTrend> {
        val workOrders = workOrderRepository.findByClientId(clientId)
        val completionTrends = mutableMapOf<LocalDate, MutableList<Long>>()

        workOrders.filter { it.status == ContractStatus.COMPLETED }.forEach { workOrder ->
            val completionDate = workOrder.dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val duration = ChronoUnit.DAYS.between(workOrder.startDate.toInstant(), workOrder.dueDate.toInstant())
            completionTrends.computeIfAbsent(completionDate) { mutableListOf() }.add(duration)
        }

        return completionTrends.map {
            AverageCompletionTrend(
                it.key,
                it.value.average()
            )
        }.sortedBy { it.date }
    }

    private fun List<Long>.average(): Double {
        return if (isEmpty()) 0.0 else sum().toDouble() / size
    }

    // get work orders by service type
    fun getClientWorkOrdersByService(clientId: String): List<ServiceTypeMetric> {
        val workOrders = workOrderRepository.findByClientId(clientId)
        val serviceTypeCounts = mutableMapOf<String, Int>()

        workOrders.forEach { workOrder ->
            val serviceType = workOrder.service
            serviceTypeCounts[serviceType] = serviceTypeCounts.getOrDefault(serviceType, 0) + 1
        }

        return serviceTypeCounts.map { ServiceTypeMetric(it.key, it.value) }
    }

    // get client suppliers by service
    fun getClientSuppliersByService(clientId: String): List<ServiceTypeMetric> {
        val client = clientRepository.findClientById(clientId)
        val serviceTypeCounts = mutableMapOf<String, Int>()

        if (client != null) {
            client.suppliers?.forEach { supplier ->
                supplier.services.forEach { serviceType ->
                    serviceTypeCounts[serviceType] = serviceTypeCounts.getOrDefault(serviceType, 0) + 1
                }
            }
        }

        return serviceTypeCounts.map { ServiceTypeMetric(it.key, it.value) }
    }
}
