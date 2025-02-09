package com.supplier_management_service.supplier_management_service.config.security

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.io.IOException

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class ResponseHeadersFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {
        val httpResponse = response as HttpServletResponse

        httpResponse.setIntHeader("X-XSS-Protection", 0)
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
        httpResponse.setHeader("X-Frame-Options", "deny")
        httpResponse.setHeader("X-Content-Type-Options", "nosniff")
        httpResponse.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors 'none';")
        httpResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate")
        httpResponse.setHeader(HttpHeaders.PRAGMA, "no-cache")
        httpResponse.setIntHeader(HttpHeaders.EXPIRES, 0)

        chain.doFilter(request, response)
    }
}
