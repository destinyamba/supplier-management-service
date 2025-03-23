package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.response.*
import com.supplier_management_service.supplier_management_service.services.MetricsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/v1/metrics")
class MetricsController(private val metricsService: MetricsService) {
    private val logger: Logger = LoggerFactory.getLogger(MetricsController::class.java)

    @GetMapping("/total-suppliers")
    fun getTotalSuppliers(@RequestParam clientId: String): ResponseEntity<Int> {
        return try {
            val totalNumberOfSuppliers = metricsService.clientTotalSuppliers(clientId)
            ResponseEntity.ok(totalNumberOfSuppliers)
        } catch (e: Exception) {
            logger.error("Failed to get total number of suppliers associated with client.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/total-work-orders")
    fun getTotalWO(@RequestParam clientId: String): ResponseEntity<Int> {
        return try {
            val totalNumberWO = metricsService.clientTotalWorkOrders(clientId)
            ResponseEntity.ok(totalNumberWO)
        } catch (e: Exception) {
            logger.error("Failed to get total number of work orders associated with client.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/upcoming-work-orders")
    fun getTotalUpcomingWO(@RequestParam clientId: String): ResponseEntity<Int> {
        return try {
            val totalNumberWO = metricsService.getUpcomingWorkOrders(clientId)
            ResponseEntity.ok(totalNumberWO)
        } catch (e: Exception) {
            logger.error("Failed to get total number of upcoming work orders associated with client.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/requirement-status")
    fun getSupplierRequirementStatusCount(@RequestParam clientId: String): ResponseEntity<RequirementStatusMetric> {
        return try {
            val requirementStatusCount = metricsService.getClientSuppliersRequirementStatusCount(clientId)
            ResponseEntity.ok(requirementStatusCount)
        } catch (e: Exception) {
            logger.error("Failed to get requirement status count.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/work-status")
    fun getSupplierWorkStatusCount(@RequestParam clientId: String): ResponseEntity<WorkStatusMetric> {
        return try {
            val workStatusCount = metricsService.getClientSuppliersWorkStatusCount(clientId)
            ResponseEntity.ok(workStatusCount)
        } catch (e: Exception) {
            logger.error("Failed to get work status count.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/contract-type")
    fun getSupplierContractTypeCount(@RequestParam clientId: String): ResponseEntity<ContractTypeMetric> {
        return try {
            val contractTypeCount = metricsService.getClientSuppliersContractTypeCount(clientId)
            ResponseEntity.ok(contractTypeCount)
        } catch (e: Exception) {
            logger.error("Failed to get contract type count.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/work-order-status")
    fun getWOStatusCount(@RequestParam clientId: String): ResponseEntity<WorkOrderStatusMetric> {
        return try {
            val woStatusCount = metricsService.getClientWOStatusCount(clientId)
            ResponseEntity.ok(woStatusCount)
        } catch (e: Exception) {
            logger.error("Failed to get work order status count.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/suppliers-onboarded-over-time")
    fun getClientSuppliersOnboardedOverTime(@RequestParam clientId: String): ResponseEntity<List<OnboardingTrend>> {
        return try {
            val onboardingTime = metricsService.getSuppliersOnboardedOverTime(clientId)
            ResponseEntity.ok(onboardingTime)
        } catch (e: Exception) {
            logger.error("Failed to get suppliers onboarded over time.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/average-work-order-completion")
    fun getAverageCompletionTime(@RequestParam clientId: String): ResponseEntity<List<AverageCompletionTrend>> {
        return try {
            val woTime = metricsService.getWOAverageCompletion(clientId)
            ResponseEntity.ok(woTime)
        } catch (e: Exception) {
            logger.error("Failed to get work order average completion time.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/work-order-service")
    fun getWOByService(@RequestParam clientId: String): ResponseEntity<List<ServiceTypeMetric>> {
        return try {
            val woTime = metricsService.getClientWorkOrdersByService(clientId)
            ResponseEntity.ok(woTime)
        } catch (e: Exception) {
            logger.error("Failed to get work order by service.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/suppliers-by-service")
    fun getSuppliersByService(@RequestParam clientId: String): ResponseEntity<List<ServiceTypeMetric>> {
        return try {
            val woTime = metricsService.getClientWorkOrdersByService(clientId)
            ResponseEntity.ok(woTime)
        } catch (e: Exception) {
            logger.error("Failed to get suppliers by service.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
