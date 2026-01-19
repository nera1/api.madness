package api.madn.es.auth.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService {

    @Transactional
    fun signUp(email: String, password: String): Long {
        return 0L
    }
}