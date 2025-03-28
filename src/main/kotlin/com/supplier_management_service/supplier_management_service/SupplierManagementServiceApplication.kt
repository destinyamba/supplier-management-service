package com.supplier_management_service.supplier_management_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
class SupplierManagementServiceApplication

fun main(args: Array<String>) {
	runApplication<SupplierManagementServiceApplication>(*args)
}