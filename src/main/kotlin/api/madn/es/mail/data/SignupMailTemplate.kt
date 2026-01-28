package api.madn.es.mail.data

import api.madn.es.mail.domain.SignupMailModel

class SignupMailTemplate(
    override val model: SignupMailModel,
) : MailTemplate {
    override val subject = "회원가입을 환영합니다, ${model.displayName}님"
}