package com.supplier_management_service.supplier_management_service.models

import org.springframework.data.annotation.Id

data class ClientsDTO(
    @Id val id: String? = null,
    val clientName: String,
    val primaryContact: PrimaryContact,
    val secondaryContact: SecondaryContact
)

data class PrimaryContact(
    val primaryContactEmail: String,
    val primaryContactName: String,
    val secondaryContactPhone: String?
)

data class SecondaryContact(
    val secondaryContactEmail: String,
    val secondaryContactName: String,
    val secondaryContactPhone: String
)
