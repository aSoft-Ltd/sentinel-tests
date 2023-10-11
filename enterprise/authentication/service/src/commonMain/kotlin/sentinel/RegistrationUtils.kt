package sentinel

import koncurrent.later.await
import raven.MailBox
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams

suspend fun RegistrationService.register(
    mailbox: MailBox,
    name: String,
    email: String,
    password: String,
) {
    val params1 = SignUpParams(name,email)
    val res = signUp(params1).await()
    val params2 = SendVerificationLinkParams(email = res.email, link = "https://test.com")

    sendVerificationLink(params2).await()

    val token = mailbox.load().await().first { msg ->
        msg.to.map { it.email.value }.contains(res.email)
    }.body.split(" ").last()

    verify(VerificationParams(email = res.email, token = token)).await()

    createUserAccount(UserAccountParams(email,password,token)).await()
}