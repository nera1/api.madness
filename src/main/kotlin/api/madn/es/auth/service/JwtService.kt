package api.madn.es.auth.service

import api.madn.es.auth.config.JwtProperties
import org.springframework.stereotype.Service

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {
}