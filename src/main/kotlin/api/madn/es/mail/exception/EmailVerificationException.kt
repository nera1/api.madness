package api.madn.es.mail.exception

import api.madn.es.common.exception.CommonException

class VerificationCodeNotFoundException : CommonException("Verification code not found")
class VerificationCodeExpiredException : CommonException("Verification code expired")
class VerificationCodeAlreadyUsedException : CommonException("Verification code already used")
class VerificationCodeMismatchException : CommonException("Verification code does not match")