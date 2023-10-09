@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel.fields

import neat.required
import symphony.Fields
import symphony.text
import kotlin.js.JsExport

class PasswordForgotFields : Fields<PasswordForgotOutput>(PasswordForgotOutput(email = "")) {
    val email = text(output::email) { required() }
}