package com.supplier_management_service.supplier_management_service.config

import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@RequiredArgsConstructor
class ApplicationConfig : WebMvcConfigurer {
    private val applicationProps: ApplicationProperties? = null

    override fun addCorsMappings(registry: CorsRegistry) {
        applicationProps?.let {
            registry.addMapping("/**")
                .allowedOrigins(it.clientOriginUrl)
                .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
                .allowedMethods(HttpMethod.GET.name())
                .maxAge(86400)
        }
    }
}
