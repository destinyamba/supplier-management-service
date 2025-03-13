package com.supplier_management_service.supplier_management_service.repositories

import com.supplier_management_service.supplier_management_service.models.PendingUser
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PendingUsersRepository : MongoRepository<PendingUser, String?> {
    fun findUserByEmail(email: String): PendingUser
}
