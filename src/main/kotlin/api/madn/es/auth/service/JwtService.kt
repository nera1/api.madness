package api.madn.es.auth.service

import org.springframework.stereotype.Service

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {
}