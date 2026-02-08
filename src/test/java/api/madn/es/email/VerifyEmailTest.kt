package api.madn.es.email

import api.madn.es.auth.api.AuthController
import api.madn.es.auth.data.VerifyEmailRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class VerifyEmailTest {
    @Autowired
    private lateinit var authController : AuthController

    @Test
    @DisplayName("Email Verification Controller Test")
    fun testVerifyEmailWithCorrectCode() {
        val mockRequest = VerifyEmailRequest(
            email = "test@teset.com",
            code = "123456"
        )
        authController.verifyEmail(mockRequest)
    }

    @Test
    @DisplayName("Email Verification Service Test")
    fun testVerifyEmailServiceWithWrongCode() {
        val mockRequest = VerifyEmailRequest(
            email = "test@teset",
            code = "1234569"
        )
        authController.verifyEmail(mockRequest)
    }
}