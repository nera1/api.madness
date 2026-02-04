package api.madn.es.mail.repository

import api.madn.es.mail.domain.EmailVerificationCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EmailVerificationCodeRepository : JpaRepository<EmailVerificationCode, Long> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT * 
            FROM email_verification_codes 
            WHERE email = :email
            ORDER BY created_at DESC
            LIMIT 1;
        """
    )
    fun findLatestVerificationCodeByEmail(email: String): EmailVerificationCode?
}