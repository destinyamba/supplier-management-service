package com.supplier_management_service.supplier_management_service.builders

import com.supplier_management_service.supplier_management_service.models.*
import java.util.*

class ClientsDTOBuilder {
    private var id: String? = null
    private lateinit var clientName: String
    lateinit var wcontactInfo: ContactInfo
    private var yearsOfOperation: Int = 0
    private var organization: String? = null
    private var suppliers: List<Supplier>? = emptyList()

    fun id(id: String?) = apply { this.id = id }
    fun clientName(clientName: String) = apply { this.clientName = clientName }
    fun contactInfo(contactInfo: ContactInfo) = apply { this.wcontactInfo = contactInfo }
    fun yearsOfOperation(yearsOfOperation: Int) = apply { this.yearsOfOperation = yearsOfOperation }
    fun validClient() = apply {
        this.id = UUID.randomUUID().toString()
        this.clientName = "Valid Client"
        this.wcontactInfo = ContactInfo(PrimaryContact("valid@email.com", "valid"))
        this.yearsOfOperation = 10
    }

    fun build(): ClientsDTO {
        if (!::wcontactInfo.isInitialized) {
            throw UninitializedPropertyAccessException("contactInfo has not been initialized")
        }
        return ClientsDTO(
            id = id,
            clientName = clientName,
            contactInfo = wcontactInfo,
            yearsOfOperation = yearsOfOperation,
            organization = organization ?: clientName,
            suppliers = suppliers
        )
    }
}