package api.madn.es.auth.repository

import api.madn.es.auth.domain.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
    @Modifying
    @Query(
        nativeQuery = true,
        value = """
        INSERT INTO user_credentials(user_id, email, password_hash)
        VALUES (:userId, :email, :passwordHash)
    """
    )
    fun saveUserCredential(
        @Param("userId") userId: Long,
        @Param("email") email: String,
        @Param("passwordHash") passwordHash: String
    ): Int

    @Query(
        nativeQuery = true,
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM user_credentials
                WHERE email = :email
            )
        """
    )
    fun existsEmailQuery(@Param("email") email: String): Long
}