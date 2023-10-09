@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import cinematic.LazyScene
import kase.Pending
import kase.Success
import keep.save
import koncurrent.toLater
import neat.required
import sentinel.fields.PasswordForgotFields
import sentinel.fields.PasswordForgotOutput
import symphony.Form
import symphony.toForm
import symphony.toSubmitConfig
import kotlin.js.JsExport

class PasswordForgotScene(
    private val config: AuthenticationScenesConfig<AuthenticationApi>
) : LazyScene<Form<Any, PasswordForgotOutput, PasswordForgotFields>>() {

    protected val api = config.api

    fun initialize() {
        ui.value = Success(form())
    }

    private fun form() = PasswordForgotFields().toForm(
        heading = "Forgot your password?",
        details = "Reset your credentials",
        config = config.toSubmitConfig()
    ) {
        onCancel { ui.value = Pending }
        onSubmit { output ->
            output.toLater().then {
                it::email.required
            }.andThen {
                config.cache.save(PasswordScenes.KEY_RESET_EMAIL,it)
            }.andThen {
                api.sendPasswordResetLink(it)
            }
        }
        onSuccess { email: String ->
            config.toaster.makeNewSuccess("Password reset")
                .withBody("A Password reset link has been sent to your email")
                .show()
        }
        onFailure {
            config.toaster.makeNewError("Password reset")
                .withBody(it.message ?: "Unknown error")
                .show()
        }
    }

    fun resend() = ui.value.data?.submit()
}