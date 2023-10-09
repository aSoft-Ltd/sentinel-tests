package sentinel.transformers

import sentinel.params.SignInParams
import sentinel.params.UserAccountParams

fun UserAccountParams.toSignInParams() = SignInParams(
    email = loginId,
    password = password
)