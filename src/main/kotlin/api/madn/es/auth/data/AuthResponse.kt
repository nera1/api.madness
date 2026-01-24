package api.madn.es.auth.data

data class SignupResponse (
    val userId: Long = 0,
    val email: String = "",
    val displayName: String = "",
)