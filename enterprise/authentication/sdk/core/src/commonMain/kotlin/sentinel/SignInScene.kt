@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import cinematic.Scene
import kase.LazyState
import kase.Pending
import sentinel.AuthenticationApi
import sentinel.AuthenticationScenesConfig
import sentinel.UserSession
import sentinel.fields.SignInFields
import symphony.toForm
import symphony.toSubmitConfig
import kotlin.js.JsExport

class SignInScene(config: AuthenticationScenesConfig<AuthenticationApi>) : Scene<LazyState<UserSession>>(Pending) {

    private val api = config.api
    private var successHandler: ((UserSession) -> Unit)? = null

    val form = SignInFields().toForm(
        heading = "Sign In",
        details = "Log in to your space",
        config = config.toSubmitConfig()
    ) {
        onSubmit { api.signIn(it.toParams()) }
        onSuccess { successHandler?.invoke(it) }
    }

    fun initialize(onSuccess: (UserSession) -> Unit) {
        successHandler = onSuccess
    }

    fun deInitialize() {
        form.exit()
        successHandler = null
        ui.value = Pending
    }
}