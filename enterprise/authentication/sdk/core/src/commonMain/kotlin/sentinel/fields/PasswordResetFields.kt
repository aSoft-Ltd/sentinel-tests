@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel.fields

import neat.check
import neat.execute
import neat.min
import neat.required
import symphony.Fields
import symphony.password
import symphony.text
import kotlin.js.JsExport

class PasswordResetFields : Fields<PasswordResetOutput>(PasswordResetOutput()) {
    val password1 = password(
        name = output::password1,
        label = "Secure Password",
        hint = "secure-password"
    ) {
        min(8)
        required()
    }

    val password2 = password(
        name = output::password2,
        label = "Confirm Password",
        hint = "confirm-password"
    ) {
        min(8)
        check(message = { "Passwords do not match" }) { it == password1.output }
        required()
    }
}