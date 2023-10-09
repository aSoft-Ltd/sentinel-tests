package sentinel.fields

import sentinel.params.SignInParams

data class SignInFormOutput(
    var email: String? = "",
    var password: String? = "",
) {
    fun toParams() = SignInParams(
        email = email ?: throw IllegalArgumentException("Email must not be null"),
        password = password ?: throw IllegalArgumentException("password must not be null"),
    )
}