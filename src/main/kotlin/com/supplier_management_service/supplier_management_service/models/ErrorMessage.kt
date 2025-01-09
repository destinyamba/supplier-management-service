package com.supplier_management_service.supplier_management_service.models

import lombok.Value

@Value
class ErrorMessage(message: String?) {
    private val message: String? = null

    companion object {
        fun from(message: String?): ErrorMessage {
            return ErrorMessage(message)
        }
    }
}
