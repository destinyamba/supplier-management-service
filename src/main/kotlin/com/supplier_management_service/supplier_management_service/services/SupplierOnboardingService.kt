package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.Supplier
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import org.springframework.stereotype.Service

@Service
class SupplierOnboardingService(private val supplierRepository: SupplierRepository) {

    fun onboardSupplier(supplierDto: Supplier): Supplier {
        // Validate supplier data
        if (supplierDto.supplierName.isBlank()) {
            throw IllegalArgumentException("Supplier name cannot be empty")
        }

        // Check if supplier already exists
        supplierRepository.findBySupplierName(supplierDto.supplierName)?.let {
            throw IllegalStateException("Supplier with name ${supplierDto.supplierName} already exists")
        }

        return try {
            supplierRepository.save(supplierDto)
        } catch (e: Exception) {
            throw RuntimeException("Failed to save supplier: ${e.message}", e)
        }
    }

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
}