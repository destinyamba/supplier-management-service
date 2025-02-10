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
class SupplierController(
    val supplierOnboardingService: SupplierOnboardingService,
) {
    private val logger = LoggerFactory.getLogger(SupplierController::class.java)

    //    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'CONTRIBUTOR')")
    @PostMapping("/onboard-supplier", consumes = ["multipart/form-data"])
    fun onboardSupplierv2(
        @RequestParam("supplierData") supplierJson: String,
        @RequestParam("coi", required = false) coi: MultipartFile?,
        @RequestParam("safetyProgram", required = false) safetyProgram: MultipartFile?,
        @RequestParam("oshaLogs", required = false) oshaLogs: MultipartFile?,
        @RequestParam("bankInfo", required = false) bankInfo: MultipartFile?
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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'CONTRIBUTOR')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
    @DeleteMapping("/{id}")
    fun deleteSupplier(@PathVariable id: String): ResponseEntity<Void> {
        return try {
            supplierOnboardingService.deleteSupplier(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'CONTRIBUTOR')")
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