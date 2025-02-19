package com.supplier_management_service.supplier_management_service.repositories

import com.supplier_management_service.supplier_management_service.models.ClientsDTO
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface ClientRepository : MongoRepository<ClientsDTO, String> {
    fun findByClientName(clientName: String): ClientsDTO?
    fun findClientById(id: String): ClientsDTO?
    fun existsByContactInfoPrimaryContactPrimaryContactEmail(primaryContactEmail: String): Boolean
    fun existsByClientName(clientName: String): Boolean
    fun findByContactInfo_PrimaryContact_PrimaryContactEmail(primaryContactEmail: String): ClientsDTO?
}
