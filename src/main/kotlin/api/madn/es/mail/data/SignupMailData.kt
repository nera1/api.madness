package api.madn.es.mail.data

data class SignupMailData(
    val displayName: String,
    val email: String,
    val description: String = "요청을 완료하려면 아래 버튼을 눌러 인증을 완료해 주세요.",
    val verifyUrl: String = "", // 기본값 말고 실제 토큰 붙여서 넣는 걸 권장
    val expiresInMinutes: Int = 10,
    val url: String = "https://madn.es",
    val year: Int = java.time.Year.now().value,
    val brandName: String = "MADN.ES",
)

