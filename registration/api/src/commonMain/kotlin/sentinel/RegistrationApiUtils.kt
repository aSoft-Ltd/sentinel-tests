package sentinel

import koncurrent.later.await
import raven.EmailReceiver
import raven.MailBox
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams

suspend fun RegistrationApi.register(
    receiver: EmailReceiver,
    name: String,
    email: String,
    password: String,
) {
    val params1 = SignUpParams(name,email)
    val res = signUp(params1).await()

    val message = receiver.anticipate()

    sendVerificationLink(email).await()

    val token = message.await().body.split(" ").last()

    verify(VerificationParams(email = res.email, token = token)).await()

    createUserAccount(UserAccountParams(email,password,token)).await()
}