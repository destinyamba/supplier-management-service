package com.supplier_management_service.supplier_management_service.repositories

import com.supplier_management_service.supplier_management_service.models.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String?> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User?
}
