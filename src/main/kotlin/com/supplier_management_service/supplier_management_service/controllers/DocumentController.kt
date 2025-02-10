package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.services.AzureBlobStorageService
import com.supplier_management_service.supplier_management_service.services.SupplierOnboardingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/suppliers/{supplierId}/documents")
class DocumentController(
    private val azureStorageService: AzureBlobStorageService,
    private val supplierService: SupplierOnboardingService
) {

    @PostMapping("/upload-file")
    fun uploadFile(
        @PathVariable supplierId: String,
        @RequestParam file: MultipartFile
    ): ResponseEntity<SupplierResponse> {
        val supplier = supplierService.getSupplier(supplierId)
        val fileUrl = azureStorageService.uploadFile(file, supplierId)

        supplier.safetyAndCompliance.coiUrl = supplier.safetyAndCompliance.copy(coiUrl = fileUrl).toString()
        supplier.safetyAndCompliance.bankInfoUrl = supplier.safetyAndCompliance.copy(bankInfoUrl = fileUrl).toString()
        supplier.safetyAndCompliance.oshaLogsUrl = supplier.safetyAndCompliance.copy(oshaLogsUrl = fileUrl).toString()
        supplier.safetyAndCompliance.safetyProgramUrl = supplier.safetyAndCompliance.copy(safetyProgramUrl = fileUrl).toString()
        return ResponseEntity.ok(SupplierResponse(supplierService.updateSupplierSafetyAndCompliance(supplier)))
    }

}