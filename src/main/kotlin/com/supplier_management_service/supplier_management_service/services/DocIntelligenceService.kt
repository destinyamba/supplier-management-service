package com.supplier_management_service.supplier_management_service.services

import com.azure.ai.documentintelligence.DocumentIntelligenceClient
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder
import com.azure.ai.documentintelligence.models.AnalyzeDocumentRequest
import com.azure.ai.documentintelligence.models.AnalyzeResult
import com.azure.core.credential.AzureKeyCredential
import com.supplier_management_service.supplier_management_service.enums.DocumentType
import com.supplier_management_service.supplier_management_service.enums.DocumentValidationResult
import com.supplier_management_service.supplier_management_service.models.RequirementStatus
import com.supplier_management_service.supplier_management_service.models.WorkStatus
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class DocIntelligenceService(private val supplierRepository: SupplierRepository) {

    private val logger = LoggerFactory.getLogger(DocIntelligenceService::class.java)

    companion object {
        private const val ENDPOINT = "https://text-extraction-destiny.cognitiveservices.azure.com/"
        private const val KEY = "7PhBPVigFJ0vo13FGEGcE4TVuHut6KpdqOTbugz4LKD1lmb5X5pTJQQJ99BBACYeBjFXJ3w3AAALACOGAPiN"
    }

    private val client: DocumentIntelligenceClient = DocumentIntelligenceClientBuilder()
        .credential(AzureKeyCredential(KEY))
        .endpoint(ENDPOINT)
        .buildClient()

    @Transactional
    fun analyzeAndValidateDocument(supplierId: String, documentUrl: String): String {
        val analyzeResult = analyzeDocument(documentUrl)
        val title = analyzeResult.documents[0].fields["title"]?.content ?: "Unknown"
        val expiryDate = analyzeResult.documents[0].fields["expiry date"]?.content
        val validation = validateDocument(title, expiryDate)

        logger.info("title: $title")

        val supplier = supplierRepository.findById(supplierId)
            .orElseThrow { Exception("Supplier not found") }

        val submittedDocuments = supplier.safetyAndCompliance.submittedDocuments.toMutableMap()

        if (validation.documentType != null) {
            submittedDocuments[validation.documentType] = validation.isValid
            supplier.safetyAndCompliance.submittedDocuments = submittedDocuments

            // Check if all required documents are submitted and valid

            supplierRepository.save(supplier)
            return "Document processed: ${validation.reason}"
        } else {
            return "Rejected: ${validation.reason}"
        }
    }

    private fun analyzeDocument(documentUrl: String): AnalyzeResult {
        val modelId = "general-safety-compliance-docs"
        val analyzeLayoutPoller = client.beginAnalyzeDocument(
            modelId,
            null, null, null, null, null, null,
            AnalyzeDocumentRequest().setUrlSource(documentUrl)
        )

        return analyzeLayoutPoller.finalResult.analyzeResult
    }

    fun validateDocument(title: String, expiryDate: String?): DocumentValidationResult {
        val type = DocumentType.entries.find { title.contains(it.keyword, ignoreCase = true) }

        return when (type) {
            DocumentType.COI -> {
                if (expiryDate.isNullOrBlank()) DocumentValidationResult(type, false, "Missing expiry date")
                else DocumentValidationResult(type, true, "Valid COI document")
            }

            DocumentType.BANK_INFO -> DocumentValidationResult(type, true, "Valid Bank Info document")
            DocumentType.OSHA_LOG -> DocumentValidationResult(type, true, "Valid OSHA Log document")
            null -> DocumentValidationResult(null, false, "Unknown document type")
        }
    }
}
