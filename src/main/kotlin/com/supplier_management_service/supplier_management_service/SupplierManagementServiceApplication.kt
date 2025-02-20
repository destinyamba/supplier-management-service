package com.supplier_management_service.supplier_management_service

import org.apache.logging.log4j.LogManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
class SupplierManagementServiceApplication

private val log = LogManager.getLogger(SupplierManagementServiceApplication::class.java)


fun main(args: Array<String>) {
	runApplication<SupplierManagementServiceApplication>(*args)
}