package com.supplier_management_service.supplier_management_service.config

import lombok.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding


@Value
@ConfigurationProperties(prefix = "application")
class ApplicationProperties
@ConstructorBinding constructor(var clientOriginUrl: String)