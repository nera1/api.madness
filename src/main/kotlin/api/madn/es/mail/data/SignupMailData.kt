package api.madn.es.mail.data

data class SignupMailData(
    override val to: String,
    val displayName : String,
) : MailTemplateData {
    override val templateName = "signup-mail"
    override val subject: String = "회원가입을 환영합니다, ${displayName}님"
}

