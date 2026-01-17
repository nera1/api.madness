package api.madn.es.auth.repository

import api.madn.es.auth.domain.User
import api.madn.es.auth.domain.UserStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface UserRepository : JpaRepository<User, Long> {
    @Modifying
    @Transactional
    @Query(
        nativeQuery = true,
        value = """
            INSERT INTO users (display_name, status)
            VALUES (:displayName, :status)
        """
    )
    fun save(
        @Param("displayName") displayName: String?,
        @Param("status") status: UserStatus? = UserStatus.SUSPENDED
    ): Int
}