package com.supplier_management_service.supplier_management_service

import io.github.cdimascio.dotenv.Dotenv
import org.apache.logging.log4j.LogManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import java.util.*
import kotlin.system.exitProcess


@SpringBootApplication
@ConfigurationPropertiesScan
class SupplierManagementServiceApplication

enum class DotEnv {
	PORT,
	CLIENT_ORIGIN_URL,
	OKTA_OAUTH2_ISSUER,
	OKTA_OAUTH2_AUDIENCE
}

private val log = LogManager.getLogger(SupplierManagementServiceApplication::class.java)


fun main(args: Array<String>) {
	dotEnvSafeCheck()
	runApplication<SupplierManagementServiceApplication>(*args)
}

private fun dotEnvSafeCheck() {
	val dotenv = Dotenv.configure()
		.ignoreIfMissing()
		.load()

	Arrays.stream<DotEnv>(DotEnv.entries.toTypedArray())
		.map<String> { obj: DotEnv -> obj.name }
		.filter { varName: String? -> dotenv.get(varName, "").isEmpty() }
		.findFirst()
		.ifPresent { varName: String? ->
			log.error(
				"[Fatal] Missing or empty environment variable: {}",
				varName
			)
			exitProcess(1)
		}
}
