package com.supplier_management_service.supplier_management_service.services

import com.azure.storage.blob.BlobClientBuilder
import com.azure.storage.blob.BlobServiceClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AzureBlobStorageService(
    @Value("\${spring.azure.storage.connection-string}")
    private val connectionString: String,

    @Value("\${spring.azure.storage.container-name}")
    private val containerName: String
) {
    private val blobServiceClient = BlobServiceClientBuilder()
        .connectionString(connectionString)
        .buildClient()
    private val logger = LoggerFactory.getLogger(AzureBlobStorageService::class.java)

    fun uploadFile(file: MultipartFile, supplierId: String): String {
        logger.info("Uploading file: ${file.originalFilename}")

        val containerClient = blobServiceClient.getBlobContainerClient(containerName)
        val blobName = "supplier-$supplierId/${UUID.randomUUID()}-${file.originalFilename}"
        val blobClient = containerClient.getBlobClient(blobName)

        blobClient.upload(file.inputStream, file.size)
        return blobClient.blobUrl
    }
    
}
