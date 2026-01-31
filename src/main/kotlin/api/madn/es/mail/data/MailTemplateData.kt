package api.madn.es.mail.data

interface MailTemplateData {
    val subject: String
    val to : String
    val templateName : String
}