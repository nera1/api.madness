package api.madn.es.mail.event

data class EmailVerificationRequestedEvent(
    val email: String,
    val displayName: String
)