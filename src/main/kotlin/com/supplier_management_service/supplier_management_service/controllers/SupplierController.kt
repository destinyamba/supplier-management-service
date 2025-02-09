package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.Supplier
import com.supplier_management_service.supplier_management_service.services.SupplierOnboardingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/supplier")
class SupplierController(
    val supplierOnboardingService: SupplierOnboardingService,
) {

    @PostMapping("/")
    fun onboardSupplier(@RequestBody supplierRequest: Supplier): ResponseEntity<Supplier> {
        return try {
            val supplier = supplierOnboardingService.onboardSupplier(supplierRequest)
            ResponseEntity(supplier, HttpStatus.CREATED)
        } catch (e: RuntimeException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/all")
    fun allPatients(
        @RequestParam(required = false, defaultValue = "1") pageNum: Int,
        @RequestParam(required = false, defaultValue = "12") pageSize: Int
    ): ResponseEntity<PagedResponse<SupplierResponse>> {
        return try {
            val response = supplierOnboardingService.getAllSuppliers(pageNum, pageSize)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSupplier(@PathVariable id: String): ResponseEntity<Void> {
        return try {
            supplierOnboardingService.deleteSupplier(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}