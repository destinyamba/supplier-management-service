package com.supplier_management_service.supplier_management_service.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.Supplier
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import com.supplier_management_service.supplier_management_service.repositories.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SupplierOnboardingService(
    private val supplierRepository: SupplierRepository,
    private val azureBlobStorageService: AzureBlobStorageService,
    private val objectMapper: ObjectMapper,
    private val userRepository: UserRepository
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

    fun getSupplier(id: String): Supplier {
        return supplierRepository.findById(id).orElseThrow { IllegalArgumentException("Supplier not found") }
    }

    fun updateSupplierSafetyAndCompliance(supplier: Supplier): Supplier {
        return supplierRepository.save(supplier)
    }

    fun onboardSupplier(supplierJson: String, files: Map<String, MultipartFile?>): Supplier {
        val supplierDto = objectMapper.readValue(supplierJson, Supplier::class.java)
        val savedSupplier = supplierRepository.save(supplierDto)

        // using supplierJson primaryContactEmail, find user and update UserDetails organization to supplierName
        val user = userRepository.findByEmail(supplierDto.contactInfo.primaryContact.primaryContactEmail)
        logger.info("user: $user")
        // update user organization to supplierDta supplier name
        user?.organizationName = supplierDto.supplierName
        logger.info("updated user: $user")
        userRepository.save(user!!)

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
    
}