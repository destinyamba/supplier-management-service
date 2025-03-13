package com.supplier_management_service.supplier_management_service.services

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException


@Service
class EmailService {

    @Value("\${spring.sendgrid.api-key}")
    private lateinit var sendGridApiKey: String

    private val logger: Logger = LoggerFactory.getLogger(EmailService::class.java)


    fun sendResetToken(userEmail: String, token: String) {
        try {
            val from = Email().apply { email = "support@zenflouu.com" }
            val to = Email().apply { email = userEmail }
            val subject = "Password Reset Request"
            val templatePath = "/Users/destinyamba/Documents/Repos/supplier-management-service/src/main/resources/templates/reset-password.html"
            var htmlContent = File(templatePath).readText()
            htmlContent = htmlContent.replace("{{token}}", token)
            val content = Content("text/html", htmlContent)
            val mail = Mail(from, subject, to, content)

            val sg = SendGrid(sendGridApiKey)
            val request = Request().apply {
                method = Method.POST
                endpoint = "mail/send"
                body = mail.build()
            }

            // ACTUALLY SEND THE REQUEST
            val response = sg.api(request)

            // Log the response for debugging
            logger.info("Email send status: ${response.statusCode}")
            logger.info("Email send response body: ${response.body}")
            logger.info("Email send response headers: ${response.headers}")

            // Handle non-2xx status codes
            if (response.statusCode !in 200..299) {
                logger.error("Failed to send email. Status: ${response.statusCode}")
                throw EmailSendException("Failed to send password reset email")
            }
        } catch (ex: IOException) {
            logger.error("Network error sending email: ${ex.message}")
            throw EmailSendException("Error sending email", ex)
        } catch (ex: Exception) {
            logger.error("Unexpected error sending email: ${ex.message}")
            throw EmailSendException("Error sending email", ex)
        }
    }

    fun sendSetPassword(userEmail: String) {
        try {
            val from = Email().apply { email = "support@zenflouu.com" }
            val to = Email().apply { email = userEmail }
            val subject = "User Invite Request"
            val resetLink = "http://localhost:3000/auth/user-invite-set-password"
            // Read the HTML template file
            val templatePath = "/Users/destinyamba/Documents/Repos/supplier-management-service/src/main/resources/templates/user-password-set.html"
            var htmlContent = File(templatePath).readText()

            // Replace the placeholder with the actual reset link
            htmlContent = htmlContent.replace("{{resetLink}}", resetLink)

            // Create content with the proper type and value
            val content = Content("text/html", htmlContent)
            val mail = Mail(from, subject, to, content)

            val sg = SendGrid(sendGridApiKey)
            val request = Request().apply {
                method = Method.POST
                endpoint = "mail/send"
                body = mail.build()
            }

            // ACTUALLY SEND THE REQUEST
            val response = sg.api(request)

            // Log the response for debugging
            logger.info("Email send status: ${response.statusCode}")
            logger.info("Email send response body: ${response.body}")
            logger.info("Email send response headers: ${response.headers}")

            // Handle non-2xx status codes
            if (response.statusCode !in 200..299) {
                logger.error("Failed to send email. Status: ${response.statusCode}")
                throw EmailSendException("Failed to send password reset email")
            }
        } catch (ex: IOException) {
            logger.error("Network error sending email: ${ex.message}")
            throw EmailSendException("Error sending email", ex)
        } catch (ex: Exception) {
            logger.error("Unexpected error sending email: ${ex.message}")
            throw EmailSendException("Error sending email", ex)
        }
    }

    // Custom exception class
    class EmailSendException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
}
