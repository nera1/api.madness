package api.madn.es.auth.service

import api.madn.es.auth.domain.User
import api.madn.es.auth.domain.UserCredential
import api.madn.es.auth.dto.SignUpRequest
import api.madn.es.auth.repository.UserCredentialRepository
import api.madn.es.auth.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    val userCredentialRepo: UserCredentialRepository,
    val userRepo: UserRepository,
    val passwordEncoder : PasswordEncoder
) {
    private fun emailExists(email: String) : Boolean = userCredentialRepo.existsEmailQuery(email) == 1L

    @Transactional
    fun signUp(request: SignUpRequest): Long {
        if (emailExists(request.email)) return 1L

        val user = userRepo.saveAndFlush(User(request.displayName))
        val encoded = passwordEncoder.encode(request.password)

        userCredentialRepo.save(UserCredential(user.id!!, request.email, encoded))
        return 0L
    }

}