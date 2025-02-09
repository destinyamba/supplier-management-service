package com.supplier_management_service.supplier_management_service.config.store

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MongoConfig {
    @Bean
    fun mongoTemplate(mongoDatabaseFactory: MongoDatabaseFactory): MongoTemplate {
        return MongoTemplate(mongoDatabaseFactory)
    }
}

