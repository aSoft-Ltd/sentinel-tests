package sentinel.fields

import identifier.params.PasswordParams
import kase.catching
import neat.required

class ChangePasswordOutput(
    var current: String? = "",
    var password1: String? = "",
    var password2: String? = ""
) {
    fun toParams() = catching {
        val current = ::password1.required
        if(password1!=password2) throw IllegalArgumentException("The passwords do not match")
        if(current.isBlank()) throw IllegalArgumentException("Current password can never be blank")
        PasswordParams(
            previous = this::current.required,
            current = current
        )
    }
}