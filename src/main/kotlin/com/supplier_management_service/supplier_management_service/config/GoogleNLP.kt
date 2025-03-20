package com.supplier_management_service.supplier_management_service.config

import com.google.cloud.language.v1.LanguageServiceClient
import com.google.cloud.language.v1.LanguageServiceSettings
import com.google.auth.oauth2.ServiceAccountCredentials
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class GoogleNLPConfig {

    @Bean
    fun languageServiceClient(): LanguageServiceClient {
        val credentialsPath = "/Users/destinyamba/Documents/Repos/supplier-management-service/galvanized-opus-454112-g7-da6240fb24d5.json"
        val credentials = ServiceAccountCredentials.fromStream(FileInputStream(credentialsPath))
        val settings = LanguageServiceSettings.newBuilder()
            .setCredentialsProvider { credentials }
            .build()

        return LanguageServiceClient.create(settings)
    }
}
