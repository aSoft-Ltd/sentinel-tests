@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel.fields

import symphony.Fields
import symphony.email
import symphony.password
import kotlin.js.JsExport

class SignInFields : Fields<SignInFormOutput>(SignInFormOutput("", "")) {

    val email = email(
        name = output::email,
        label = "Enter email address",
        hint = "john@doe.com"
    )

    val password = password(
        name = output::password,
        label = "Password",
        hint = "Secure Password"
    )

    val recoverText: String = "Forgot password?"

    val signUpPromptText: String = "New to PiMonitor? Sign Up"

    val signInText = "Sign In"
}