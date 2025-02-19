package com.supplier_management_service.supplier_management_service.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "clients")
data class ClientsDTO(
    @Id val id: String? = null,
    val clientName: String,
    val contactInfo: ContactInfo,
    val yearsOfOperation: Int,
    var organization: String = clientName,
)

data class PrimaryContact(
    val primaryContactEmail: String,
    val primaryContactName: String,
)

data class SecondaryContact(
    val secondaryContactEmail: String,
    val secondaryContactName: String,
)
