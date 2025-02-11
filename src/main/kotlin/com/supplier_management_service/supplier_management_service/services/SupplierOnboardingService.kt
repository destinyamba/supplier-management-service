package com.supplier_management_service.supplier_management_service.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.Supplier
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SupplierOnboardingService(
    private val supplierRepository: SupplierRepository,
    private val azureBlobStorageService: AzureBlobStorageService,
    private val objectMapper: ObjectMapper
) {
    private val logger: Logger = LoggerFactory.getLogger(SupplierOnboardingService::class.java)
    
    fun getAllSuppliers(pageNum: Int, pageSize: Int): PagedResponse<SupplierResponse> {
        val allPatients = supplierRepository.findAll()
        val totalPatients = allPatients.size
        val totalPages = (totalPatients + pageSize - 1) / pageSize

        val startIndex = (pageNum - 1) * pageSize
        val endIndex = (startIndex + pageSize).coerceAtMost(totalPatients)
        val paginatedPatients = if (startIndex < totalPatients) {
            allPatients.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        val patientResponses = paginatedPatients.map { SupplierResponse(it) }

        return PagedResponse(
            suppliers = patientResponses,
            page = pageNum,
            pageSize = pageSize,
            totalItems = totalPatients,
            totalPages = totalPages
        )
    }

    fun deleteSupplier(id: String) {
        return try {
            supplierRepository.deleteById(id)
        } catch (e: Exception) {
            throw RuntimeException("Failed to delete supplier: ${e.message}", e)
        }
    }

    fun getSupplier(id: String): Supplier {
        return supplierRepository.findById(id).orElseThrow { IllegalArgumentException("Supplier not found") }
    }

    fun updateSupplierSafetyAndCompliance(supplier: Supplier): Supplier {
        return supplierRepository.save(supplier)
    }

    // update onboardSupplier to upload files to azure blob storage and save file links to mongodb.
    fun onboardSupplier(supplierJson: String, files: Map<String, MultipartFile?>): Supplier {
        val supplierDto = objectMapper.readValue(supplierJson, Supplier::class.java)
        val savedSupplier = supplierRepository.save(supplierDto)

        // Upload files and update supplier
        files.forEach { (documentType, file) ->
            if (file != null && !file.isEmpty) {
                try {
                    val url = azureBlobStorageService.uploadFile(file, savedSupplier.id!!)
                    when (documentType) {
                        "coi" -> savedSupplier.safetyAndCompliance.coiUrl = url
                        "safetyProgram" -> savedSupplier.safetyAndCompliance.safetyProgramUrl = url
                        "oshaLogs" -> savedSupplier.safetyAndCompliance.oshaLogsUrl = url
                        "bankInfo" -> savedSupplier.safetyAndCompliance.bankInfoUrl = url
                    }
                } catch (e: Exception) {
                    logger.error("Failed to upload file $documentType", e)
                }
            }
        }

        return supplierRepository.save(savedSupplier)
    }

    // update deleteSupplier to delete files from azure blob storage.
    fun deleteSupplierv2(id: String) {
        val supplier = supplierRepository.findById(id).orElseThrow()

        // Delete all associated files
        listOfNotNull(
            supplier.safetyAndCompliance.coiUrl,
            supplier.safetyAndCompliance.safetyProgramUrl,
            supplier.safetyAndCompliance.oshaLogsUrl,
            supplier.safetyAndCompliance.bankInfoUrl
        ).forEach { azureBlobStorageService.deleteFile(it) }

        supplierRepository.deleteById(id)
    }

    // add update endpoint to update supplier details and files.

}