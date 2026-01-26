package api.madn.es.auth.repository

import api.madn.es.auth.domain.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
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

    @Query(
        nativeQuery = true,
        value = """
            SELECT * FROM user_credentials WHERE email = :email
        """
    )
    fun findByEmailQuery(@Param("email") email: String): UserCredential?
}