package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.response.WOPagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.WOResponse
import com.supplier_management_service.supplier_management_service.enums.Region
import com.supplier_management_service.supplier_management_service.models.WorkOrder
import com.supplier_management_service.supplier_management_service.services.WorkOrderService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/v1/work-order")
class WorkOrderController(private val workOrderService: WorkOrderService) {
    private val logger = LoggerFactory.getLogger(WorkOrderController::class.java)

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @PostMapping
    fun createWorkOrder(@RequestBody workOrder: WorkOrder): ResponseEntity<Any> {
        return try {
            val savedWorkOrder = workOrderService.createWorkOrder(workOrder)
            ResponseEntity.status(HttpStatus.CREATED).body(savedWorkOrder)
        } catch (e: Exception) {
            logger.error("Error creating WO: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating work order: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    fun getWorkOrder(@PathVariable id: String): ResponseEntity<WorkOrder> {
        return try {
            val workOrder = workOrderService.getWorkOrderById(id)
            ResponseEntity.ok(workOrder)
        } catch (e: Exception) {
            logger.info("Error getting work order. WO id probably not found.")
            ResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }

    @GetMapping("/client/{clientId}")
    fun getWorkOrdersByClientId(
        @PathVariable clientId: String,
        @RequestParam(required = false, defaultValue = "1") pageNum: Int,
        @RequestParam(required = false, defaultValue = "12") pageSize: Int
    ): ResponseEntity<WOPagedResponse<WOResponse>> {
        return try {
            val workOrders = workOrderService.listOfWOs(pageNum, pageSize, clientId)
            ResponseEntity.ok(workOrders)
        } catch (e: Exception) {
            logger.error("Error getting list of work orders. ${e.message}")
            ResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }

    @DeleteMapping("/delete/{clientId}/{woId}")
    fun cancelWO(@PathVariable clientId: String, @PathVariable woId: String): ResponseEntity<Void> {
        return try {
            workOrderService.voidWO(clientId, woId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Error deleting user: ${e.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: Exception) {
            logger.error("Error occurred while deleting user: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/locations")
    fun getRegions(@RequestParam(required = false) country: String?): List<Region> {
        return if (country.isNullOrEmpty()) {
            workOrderService.getAllRegions()
        } else {
            workOrderService.getAllRegions().filter { it.country.equals(country, ignoreCase = true) }
        }
    }
}
