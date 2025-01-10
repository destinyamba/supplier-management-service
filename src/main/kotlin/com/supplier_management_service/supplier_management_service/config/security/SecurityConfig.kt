package com.supplier_management_service.supplier_management_service.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity
class SecurityConfig(private val authenticationErrorHandler: AuthenticationErrorHandler) {

    @Bean
    @Throws(Exception::class)
    fun httpSecurity(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/messages/private", "/api/messages/admin", "/api/users").authenticated()
                    .anyRequest().permitAll()
            }
            .cors(Customizer.withDefaults())
            .oauth2ResourceServer { oauth2: OAuth2ResourceServerConfigurer<HttpSecurity?> ->
                oauth2
                    .jwt { jwt ->
                        jwt.jwtAuthenticationConverter(
                            makePermissionsConverter()
                        )
                    }
                    .authenticationEntryPoint(authenticationErrorHandler)
            }
            .build()
    }

    private fun makePermissionsConverter(): JwtAuthenticationConverter {
        val jwtAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        jwtAuthoritiesConverter.setAuthoritiesClaimName("permissions")
        jwtAuthoritiesConverter.setAuthorityPrefix("")

        val jwtAuthConverter = JwtAuthenticationConverter()
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(jwtAuthoritiesConverter)

        return jwtAuthConverter
    }
}