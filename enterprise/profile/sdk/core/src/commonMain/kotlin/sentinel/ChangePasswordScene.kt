@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import cinematic.LazyScene
import identifier.IndividualDto
import kase.Pending
import kase.toLazyState
import koncurrent.Later
import koncurrent.later.finally
import koncurrent.toLater
import sentinel.fields.ChangePasswordFields
import sentinel.fields.ChangePasswordOutput
import symphony.Form
import symphony.toForm
import symphony.toSubmitConfig
import kotlinx.JsExport

class ChangePasswordScene(
    private val config: ProfileScenesConfig<ProfileApiProvider>
) : LazyScene<Form<IndividualDto,ChangePasswordOutput,ChangePasswordFields>>(Pending) {

    fun initialize() = Later(form()).finally {
        ui.value = it.toLazyState()
    }

    private fun form() = ChangePasswordFields().toForm(
        heading = "Password form",
        details = "Change your password",
        config = config.toSubmitConfig()
    ) {
        onSubmit { output->
            output.toLater().then {
                it.toParams().getOrThrow()
            }.andThen {
                config.api.profile.personal.changePassword(it)
            }
        }
    }
}