package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.Supplier
import com.supplier_management_service.supplier_management_service.services.SupplierOnboardingService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/v1/supplier")
@CrossOrigin(origins = ["http://localhost:3000"])
class SupplierController(
    val supplierOnboardingService: SupplierOnboardingService,
) {
    private val logger = LoggerFactory.getLogger(SupplierController::class.java)

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @PostMapping("/onboard-supplier", consumes = ["multipart/form-data"])
    fun onboardSupplier(
        @RequestPart("supplierData") supplierJson: String,
        @RequestPart("coi", required = false) coi: MultipartFile?,
        @RequestPart("safetyProgram", required = false) safetyProgram: MultipartFile?,
        @RequestPart("oshaLogs", required = false) oshaLogs: MultipartFile?,
        @RequestPart("bankInfo", required = false) bankInfo: MultipartFile?
    ): ResponseEntity<Supplier> {
        return try {

            val files = mapOf(
                "coi" to coi,
                "safetyProgram" to safetyProgram,
                "oshaLogs" to oshaLogs,
                "bankInfo" to bankInfo
            ).filterValues { it != null }
            val supplier = supplierOnboardingService.onboardSupplier(supplierJson, files)
            ResponseEntity(supplier, HttpStatus.CREATED)
        } catch (e: RuntimeException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @DeleteMapping("/{id}")
    fun deleteSupplier(@PathVariable id: String): ResponseEntity<Void> {
        return try {
            supplierOnboardingService.deleteSupplier(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @GetMapping("/{id}")
    fun getSupplier(@PathVariable id: String): ResponseEntity<Supplier> {
        return try {
            val supplier = supplierOnboardingService.getSupplier(id)
            ResponseEntity(supplier, HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}