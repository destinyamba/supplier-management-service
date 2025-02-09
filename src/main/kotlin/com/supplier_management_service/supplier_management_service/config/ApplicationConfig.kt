package com.supplier_management_service.supplier_management_service.config

import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@RequiredArgsConstructor
class ApplicationConfig : WebMvcConfigurer {
    private val applicationProps: ApplicationProperties? = null

    override fun addCorsMappings(registry: CorsRegistry) {
        applicationProps?.let {
            registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
                .allowedMethods(
                    HttpMethod.GET.name(),
                    HttpMethod.POST.name(),
                    HttpMethod.PUT.name(),
                    HttpMethod.DELETE.name(),
                    HttpMethod.OPTIONS.name()
                )
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(86400)
        }
    }

    /**
     * Send Grid email template configuration
     *
     * This will allow the EmailService to send emails using the SendGrid API.
     */
    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}
