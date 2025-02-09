package com.supplier_management_service.supplier_management_service.repositories

import com.supplier_management_service.supplier_management_service.dtos.request.PasswordResetToken
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PasswordResetTokenRepository : MongoRepository<PasswordResetToken, UUID> {
    fun findByUserIdAndToken(userId: String, token: String): PasswordResetToken?
}