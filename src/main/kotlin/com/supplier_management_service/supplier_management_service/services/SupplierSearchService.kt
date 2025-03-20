package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.*
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SupplierSearchService(private val nlpService: NLPService, private val supplierRepository: SupplierRepository) {
    private val logger: Logger = LoggerFactory.getLogger(SupplierSearchService::class.java)

    // search suppliers
    fun nlpSearchSuppliers(
        query: String,
        page: Int = 0,
        pageSize: Int = 10
    ): PagedResponse<SupplierResponse> {
        val entities = extractEntities(query)

        logger.info("Query: $query")
        logger.info("Entities: $entities")

        val allSuppliers = supplierRepository.findAll()

        // Check for direct name matches first
        val nameMatches = allSuppliers.filter { supplier ->
            supplier.supplierName.contains(query, ignoreCase = true)
        }

        // If we have direct name matches, prioritize those
        if (nameMatches.isNotEmpty()) {
            return paginateResults(nameMatches, page, pageSize)
        }

        // Otherwise, check for field matches
        val fieldMatches = allSuppliers.filter { supplier ->
            // Business classification matches
            val classificationMatch = KeywordMapping.businessClassificationMapping.any { (keywordPhrase, classificationEnum) ->
                val keywordExists = query.contains(keywordPhrase.split(" ")[0], ignoreCase = true)
                val classificationKey = classificationEnum.toString()
                val supplierHasClassification = supplier.businessClassifications[classificationKey] == true

                keywordExists && supplierHasClassification
            }

            // Work status matches
            val workStatusMatch = KeywordMapping.workStatusMapping.any { (keyword, status) ->
                query.contains(keyword, ignoreCase = true) && supplier.workStatus == status
            }

            // Contract type matches
            val contractTypeMatch = KeywordMapping.contractTypeMapping.any { (keyword, type) ->
                query.contains(keyword, ignoreCase = true) && supplier.contractType == type
            }

            // Requirement status matches
            val requirementStatusMatch = KeywordMapping.requirementStatusMapping.any { (keyword, status) ->
                query.contains(keyword, ignoreCase = true) && supplier.requirementsStatus == status
            }

            // Check if any of the field conditions match
            classificationMatch || workStatusMatch || contractTypeMatch || requirementStatusMatch
        }

        // If we found matches by classification, use those
        // Otherwise, fall back to service matching
        val keywordFilteredSuppliers = fieldMatches.ifEmpty {
            allSuppliers.filter { supplier ->
                supplier.services.any { it.contains(query, ignoreCase = true) }
            }
        }

        return paginateResults(keywordFilteredSuppliers, page, pageSize)
    }

    // Helper function to paginate results
    private fun paginateResults(
        suppliers: List<Supplier>,
        page: Int,
        pageSize: Int
    ): PagedResponse<SupplierResponse> {
        // Calculate pagination values
        val totalItems = suppliers.size
        val totalPages = if (totalItems == 0) 0 else (totalItems + pageSize - 1) / pageSize
        val validPage = page.coerceIn(0, maxOf(0, totalPages - 1))

        // Apply pagination to filtered results
        val paginatedSuppliers = suppliers
            .drop(validPage * pageSize)
            .take(pageSize)
            .map { SupplierResponse(it) }

        return PagedResponse(
            suppliers = paginatedSuppliers,
            page = validPage,
            pageSize = pageSize,
            totalItems = totalItems,
            totalPages = totalPages
        )
    }

    private fun extractEntities(query: String): List<String> {
        val entities = nlpService.analyzeEntities(query).toMutableList()
        businessClassifications.forEach { classification ->
            if (query.contains(classification, ignoreCase = true)) {
                entities.add(classification)
            }
        }
        return entities
    }

    val businessClassifications = setOf(
        "veteran owned",
        "minority owned",
        "woman owned",
        "lgbtq owned",
        "disability owned",
        "small business",
        "economically disadvantaged woman owned",
        "small disadvantaged business",
        "native american owned",
        "b corporation",
        "rural owned",
        "cooperative",
        "youth owned",
        "foreign owned",
        "public benefit corporation",
        "social enterprise",
        "family owned",
        "franchise business"
    )

    object KeywordMapping {
        val contractTypeMapping = mapOf(
            "no contract" to ContractType.NO_CONTRACT,
            "direct" to ContractType.DIRECT,
            "subcontracted" to ContractType.SUBCONTRACTED
        )

        val workStatusMapping = mapOf(
            "approved" to WorkStatus.APPROVED,
            "not approved" to WorkStatus.NOT_APPROVED
        )

        val requirementStatusMapping = mapOf(
            "pending" to RequirementStatus.PENDING,
            "submitted" to RequirementStatus.SUBMITTED,
            "requirements submitted" to RequirementStatus.SUBMITTED,
            "requirements pending" to RequirementStatus.PENDING
        )

        val businessClassificationMapping = mapOf(
            "veteran owned" to IBusinessClassification.VeteranOwned,
            "woman owned" to IBusinessClassification.WomanOwned,
            "minority owned" to IBusinessClassification.MinorityOwned,
            "lgbtq owned" to IBusinessClassification.LgbtqOwned,
            "disability owned" to IBusinessClassification.DisabilityOwned,
            "small business" to IBusinessClassification.SmallBusiness,
            "economically disadvantaged woman owned" to IBusinessClassification.EconomicallyDisadvantagedWomanOwned,
            "small disadvantaged business" to IBusinessClassification.SmallDisadvantagedBusiness,
            "native american owned" to IBusinessClassification.NativeAmericanOwned,
            "b corporation" to IBusinessClassification.BCorporation,
            "rural owned" to IBusinessClassification.RuralOwned,
            "cooperative" to IBusinessClassification.Cooperative,
            "youth owned" to IBusinessClassification.YouthOwned,
            "foreign owned" to IBusinessClassification.ForeignOwned,
            "public benefit corporation" to IBusinessClassification.PublicBenefitCorporation,
            "social enterprise" to IBusinessClassification.SocialEnterprise,
            "family owned" to IBusinessClassification.FamilyOwned,
            "franchise business" to IBusinessClassification.FranchiseBusiness
        )
    }
}