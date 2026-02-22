package api.madn.es.auth.data

data class SignInResult(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val displayName: String
)