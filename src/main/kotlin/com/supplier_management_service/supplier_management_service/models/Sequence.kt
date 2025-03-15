package com.supplier_management_service.supplier_management_service.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("sequences")
data class Sequence(
    @Id
    val id: String,
    val seq: Int
)