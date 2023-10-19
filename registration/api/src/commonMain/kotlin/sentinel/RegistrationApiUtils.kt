package sentinel

import koncurrent.later.await
import raven.MailBox
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams

suspend fun RegistrationApi.register(
    mailbox: MailBox,
    name: String,
    email: String,
    password: String,
) {
    val params1 = SignUpParams(name,email)
    val res = signUp(params1).await()

    val message = mailbox.anticipate()

    sendVerificationLink(email).await()

    val token = message.await().split(" ").last()

    verify(VerificationParams(email = res.email, token = token)).await()

    createUserAccount(UserAccountParams(email,password,token)).await()
}