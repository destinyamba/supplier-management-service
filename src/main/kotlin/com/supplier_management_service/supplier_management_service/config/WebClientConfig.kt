package com.supplier_management_service.supplier_management_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import io.netty.resolver.DefaultAddressResolverGroup

@Configuration
class WebClientConfig {
    @Bean
    fun webClientBuilder(): WebClient.Builder {
        val connectionProvider = ConnectionProvider.builder("custom")
            .maxConnections(500)
            .maxIdleTime(Duration.ofSeconds(20))
            .maxLifeTime(Duration.ofSeconds(60))
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .build()

        val httpClient = HttpClient.create(connectionProvider)
            .resolver(DefaultAddressResolverGroup.INSTANCE)
            .responseTimeout(Duration.ofSeconds(10))

        return WebClient.builder()
            .clientConnector(org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
    }
}