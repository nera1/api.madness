package api.madn.es.auth.exception

import api.madn.es.common.exception.CommonException

class InvalidTokenException : CommonException("Invalid token")
class TokenExpiredException : CommonException("Token expired")
class RefreshTokenNotFoundException : CommonException("Refresh token not found")
class InvalidCredentialsException : CommonException("Invalid email or password")