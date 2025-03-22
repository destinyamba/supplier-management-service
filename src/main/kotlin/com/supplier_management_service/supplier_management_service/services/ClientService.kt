package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.ClientsDTO
import com.supplier_management_service.supplier_management_service.models.ContractType
import com.supplier_management_service.supplier_management_service.repositories.ClientRepository
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import com.supplier_management_service.supplier_management_service.repositories.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ClientService(
    private val clientRepository: ClientRepository,
    private val userRepository: UserRepository,
    private val supplierRepository: SupplierRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(ClientService::class.java)

    fun onboardClient(clientData: ClientsDTO): ClientsDTO {
        // validate email and client name
        validateEmailAndClientName(clientData)
        // find user by email and update UserDetails organization to clientName
        val user = userRepository.findByEmail(clientData.contactInfo.primaryContact.primaryContactEmail)
        user!!.organizationName = clientData.clientName
        userRepository.save(user)
        val savedClient = clientRepository.save(clientData)
        user.orgId = clientData.id

        return savedClient
    }

    private fun validateEmailAndClientName(clientData: ClientsDTO) {
        // if clientData.clientName is already in the database, throw an exception
        if (clientRepository.existsByContactInfoPrimaryContactPrimaryContactEmail(clientData.contactInfo.primaryContact.primaryContactEmail)) {
            throw RuntimeException("Client with email ${clientData.contactInfo.primaryContact.primaryContactEmail} already exists")
        }

        // if clientData.email is already in the database, throw an exception
        if (clientRepository.existsByClientName(clientData.clientName)) {
            throw RuntimeException("Client with name ${clientData.clientName} already exists")
        }
    }

    // add supplier to approved supplier list (ASL)
    fun addSupplierToList(clientId: String, supplierId: String, contractType: ContractType): ClientsDTO? {
        val client = clientRepository.findClientById(clientId)
        val supplier = supplierRepository.findSupplierById(supplierId)

        if (client != null && supplier != null) {
            // check for duplicates
            if (client.suppliers?.any { it.id == supplierId } == true) {
                logger.error("Supplier with ID $supplierId is already in the approved supplier list")
                throw RuntimeException("Supplier with ID $supplierId is already in the approved supplier list")
            }

            // copy of the supplier
            val supplierCopy = supplier.copy()

            // update the contract type of the copy
            supplierCopy.contractType = contractType

            // add the modified supplier copy to client's suppliers list
            client.suppliers = client.suppliers?.plus(supplierCopy) ?: listOf(supplierCopy)

            // save again
            clientRepository.save(client)
        }

        return client
    }

    // get list of suppliers associated with client.
    fun approvedSuppliers(clientId: String, page: Int, pageSize: Int): PagedResponse<SupplierResponse> {
        val client = clientRepository.findClientById(clientId)
        val allSuppliers = client?.suppliers ?: emptyList()

        val totalItems = allSuppliers.size
        val totalPages = if (totalItems == 0) 1 else (totalItems + pageSize - 1) / pageSize
        val validPage = page.coerceIn(0, maxOf(0, totalPages - 1))

        val paginatedSuppliers = allSuppliers
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

}
