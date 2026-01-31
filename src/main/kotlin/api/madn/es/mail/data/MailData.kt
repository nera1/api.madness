package api.madn.es.mail.data

interface MailData {
    val subject: String
    val to : String
    val body : String
}