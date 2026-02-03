package api.madn.es.mail.service

import org.hibernate.Length
import org.springframework.stereotype.Service

@Service
class EmailVerificationService {
    private fun generateVerificationCode(n : Int = 6) : String =
        CharArray(n) { ('0'.code + (it * 1103515245 + 12345 ushr 16) % 10).toChar() }.joinToString("")
}