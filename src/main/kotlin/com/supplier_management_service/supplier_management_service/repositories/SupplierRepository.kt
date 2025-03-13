package com.supplier_management_service.supplier_management_service.repositories

import com.supplier_management_service.supplier_management_service.models.Supplier
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SupplierRepository : MongoRepository<Supplier, String> {
    fun findBySupplierName(supplierName: String): Supplier?
    fun findSupplierById(id: String): Supplier?
    fun existsByContactInfoPrimaryContactPrimaryContactEmail(primaryContactEmail: String): Boolean
    fun findByContactInfo_PrimaryContact_PrimaryContactEmail(primaryContactEmail: String): Supplier?
}