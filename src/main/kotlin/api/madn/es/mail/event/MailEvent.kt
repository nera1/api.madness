package api.madn.es.mail.event

data class EmailVerificationRequestedEvent(
    val email: String,
    val displayName: String,
    val code: String
)

data class VerificationCodeSaveEvent(
    val email: String,
    val code: String
)