package com.supplier_management_service.supplier_management_service.services

import com.google.cloud.language.v1.AnalyzeEntitiesRequest
import com.google.cloud.language.v1.Document
import com.google.cloud.language.v1.LanguageServiceClient
import org.springframework.stereotype.Service


@Service
class NLPService(private val languageServiceClient: LanguageServiceClient) {
    fun analyzeEntities(text: String): List<String> {
        val doc = Document.newBuilder()
            .setContent(text)
            .setType(Document.Type.PLAIN_TEXT)
            .build()

        val response = languageServiceClient.analyzeEntities(
            AnalyzeEntitiesRequest.newBuilder().setDocument(doc).build()
        )

        return response.entitiesList.map { it.name }
    }
}

