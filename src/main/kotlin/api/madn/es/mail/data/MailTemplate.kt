package api.madn.es.mail.data

interface MailTemplate {
    val subject: String
    val model : Any
}