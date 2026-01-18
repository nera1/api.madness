package api.madn.es.auth.repository

import api.madn.es.auth.domain.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
    @Modifying
    @Query(
        nativeQuery = true,
        value = """
        INSERT INTO user_credential(user_id, email, password_hash)
        VALUES (:userId, :email, :passwordHash)
        """)
    fun saveUserCredential(userCredential: UserCredential)
}