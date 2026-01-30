package api.madn.es.mail.template

import api.madn.es.mail.data.MailTemplate
import api.madn.es.mail.data.SignupMailData

class SignupMailTemplate(
    override val model: SignupMailData,
) : MailTemplate {
    override val subject = "회원가입을 환영합니다, ${model.displayName}님"
}