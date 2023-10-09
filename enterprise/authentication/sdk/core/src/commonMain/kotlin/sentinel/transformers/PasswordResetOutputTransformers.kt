package sentinel.transformers

import kase.catching
import neat.required
import sentinel.fields.PasswordResetOutput
import sentinel.params.PasswordResetParams

fun PasswordResetOutput.toParams(token: String) = catching {
    val p1 = this::password1.required
    val p2 = this::password2.required
    if (p1 !== p2) throw IllegalArgumentException("Passwords do not match")
    PasswordResetParams(password = p2, token)
}