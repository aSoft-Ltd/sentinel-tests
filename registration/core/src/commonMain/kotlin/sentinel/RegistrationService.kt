package sentinel

import koncurrent.Later
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams

interface RegistrationService {
    fun signUp(params: SignUpParams): Later<SignUpParams>

    // One might think this should be coupled to signup method, but we separated them to be able to
    // (re)send the verification link multiple times
    // i.e. Did not get a verification link?? Click here to send again
    fun sendVerificationLink(params: SendVerificationLinkParams): Later<String>

    fun verify(params: VerificationParams): Later<VerificationParams>

    fun createUserAccount(params: UserAccountParams): Later<UserAccountParams>
}