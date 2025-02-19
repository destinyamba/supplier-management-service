package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.models.ClientsDTO
import com.supplier_management_service.supplier_management_service.repositories.ClientRepository
import com.supplier_management_service.supplier_management_service.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class ClientOnboardingService(private val clientRepository: ClientRepository, private val userRepository: UserRepository) {

    fun onboardClient(clientData: ClientsDTO): ClientsDTO {
        // validate email and client name
        validateEmailAndClientName(clientData)
        // find user by email and update UserDetails organization to clientName
        val user = userRepository.findByEmail(clientData.contactInfo.primaryContact.primaryContactEmail)
        user!!.organizationName = clientData.clientName
        userRepository.save(user)
        return clientRepository.save(clientData)
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

}
