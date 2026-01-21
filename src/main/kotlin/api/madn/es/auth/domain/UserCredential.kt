package api.madn.es.auth.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_credentials")
class UserCredential(
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(name = "email", length = 128)
    var email: String,

    @Column(name = "password_hash", length = 255)
    var passwordHash: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    val createdAt: LocalDateTime? = null

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    val updatedAt: LocalDateTime? = null
}