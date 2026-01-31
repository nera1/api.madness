package api.madn.es.mail.data

data class SignupMailData(
    override val subject: String,
    override val to: String,
    override val body: String
) : MailData {
    companion object {
        private const val filename : String = "signup-mail"
    }
}

