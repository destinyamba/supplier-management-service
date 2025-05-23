package com.supplier_management_service.supplier_management_service.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.enums.DocumentType
import com.supplier_management_service.supplier_management_service.models.RequirementStatus
import com.supplier_management_service.supplier_management_service.models.Supplier
import com.supplier_management_service.supplier_management_service.models.WorkStatus
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import com.supplier_management_service.supplier_management_service.repositories.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SupplierOnboardingService(
    private val supplierRepository: SupplierRepository,
    private val azureBlobStorageService: AzureBlobStorageService,
    private val objectMapper: ObjectMapper,
    private val userRepository: UserRepository,
    private val docIntelligenceService: DocIntelligenceService
) {
    private val logger: Logger = LoggerFactory.getLogger(SupplierOnboardingService::class.java)

    fun getAllSuppliers(pageNum: Int, pageSize: Int): PagedResponse<SupplierResponse> {
        // Filter and sort suppliers
        val allSuppliers = supplierRepository.findAll()
        val filteredSuppliers = allSuppliers.filter {
            it.requirementsStatus == RequirementStatus.SUBMITTED && it.workStatus == WorkStatus.APPROVED
            it.requirementsStatus == RequirementStatus.SUBMITTED || it.workStatus == WorkStatus.APPROVED
        }
        val remainingSuppliers = allSuppliers - filteredSuppliers.toSet()
        val sortedSuppliers = filteredSuppliers + remainingSuppliers

        val totalSuppliers = sortedSuppliers.size
        val totalPages = (totalSuppliers + pageSize - 1) / pageSize

        val startIndex = (pageNum - 1) * pageSize
        val endIndex = (startIndex + pageSize).coerceAtMost(totalSuppliers)
        val paginatedSuppliers = if (startIndex < totalSuppliers) {
            sortedSuppliers.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        val supplierResponses = paginatedSuppliers.map { SupplierResponse(it) }

        return PagedResponse(
            suppliers = supplierResponses,
            page = pageNum,
            pageSize = pageSize,
            totalItems = totalSuppliers,
            totalPages = totalPages
        )
    }

    fun getSupplier(id: String): Supplier {
        return supplierRepository.findById(id).orElseThrow { IllegalArgumentException("Supplier not found") }
    }

    fun onboardSupplier(supplierJson: String, files: Map<String, MultipartFile?>): Supplier {
        val supplierDto = objectMapper.readValue(supplierJson, Supplier::class.java)
        var currentSupplier = supplierRepository.save(supplierDto)

        // using supplierJson primaryContactEmail, find user and update UserDetails organization to supplierName
        val user = userRepository.findByEmail(supplierDto.contactInfo.primaryContact.primaryContactEmail)
        // update user organization to supplierDta supplier name
        user?.organizationName = supplierDto.supplierName
        userRepository.save(user!!)

        // Upload files currentSupplier and update supplier
        files.forEach { (documentType, file) ->
            if (file != null && !file.isEmpty) {
                try {
                    currentSupplier = supplierRepository.findById(currentSupplier.id!!).get()
                    val submittedDocuments = currentSupplier.safetyAndCompliance.submittedDocuments.toMutableMap()
                    val url = azureBlobStorageService.uploadFile(file, currentSupplier.id!!)

                    // Map documentType string to DocumentType enum
                    val mappedDocumentType = mapDocumentType(documentType)

                    // Update the submittedDocuments map with the current document type
                    submittedDocuments[mappedDocumentType] = true  // Store the URL or a flag (e.g., true)
                    currentSupplier.safetyAndCompliance.submittedDocuments = submittedDocuments

                    when (documentType) {
                        "coi" -> currentSupplier.safetyAndCompliance.coiUrl = url
                        "oshaLogs" -> currentSupplier.safetyAndCompliance.oshaLogsUrl = url
                        "bankInfo" -> currentSupplier.safetyAndCompliance.bankInfoUrl = url
                    }

                    // Check if all required document types are present in submittedDocuments
                    val allRequiredDocsSubmitted = DocumentType.entries.all { it in submittedDocuments.keys }
                    if (allRequiredDocsSubmitted) {
                        currentSupplier.requirementsStatus = RequirementStatus.SUBMITTED
                    }

                    supplierRepository.save(currentSupplier)

                } catch (e: Exception) {
                    logger.error("Failed to upload file $documentType", e)
                }
            }
        }

        // validate the submitted documents asynchronously.
        asyncTriggerDocumentAnalysis(currentSupplier.id!!)
        return currentSupplier
    }

    private fun mapDocumentType(documentType: String): DocumentType {
        val normalizedType = documentType.lowercase()

        return when {
            normalizedType.contains("coi") ||
                    normalizedType.contains("insurance") ||
                    normalizedType.contains("certificate of insurance") -> DocumentType.COI

            normalizedType.contains("osha") -> DocumentType.OSHA_LOG

            normalizedType.contains("bank") ||
                    normalizedType.contains("bank info") -> DocumentType.BANK_INFO

            else -> throw IllegalArgumentException("Invalid document type: $documentType")
        }
    }

    @Async
    fun asyncTriggerDocumentAnalysis(supplierId: String) {
        val supplier = supplierRepository.findById(supplierId).orElseThrow()
        val safety = supplier.safetyAndCompliance

        listOf(
            safety.coiUrl,
            safety.oshaLogsUrl,
            safety.bankInfoUrl
        ).forEach { url ->
            if (url != null) {
                docIntelligenceService.analyzeAndValidateDocument(supplierId, url)
            }
        }
    }

}