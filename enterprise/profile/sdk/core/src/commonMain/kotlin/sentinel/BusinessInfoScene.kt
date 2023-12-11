@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import cinematic.LazyScene
import identifier.CorporatePresenter
import identifier.IdentifierSettings
import identifier.fields.CorporateFields
import identifier.fields.CorporateOutput
import identifier.transformers.toParams
import identifier.transformers.toPresenter
import kase.Loading
import kase.toLazyState
import koncurrent.later.finally
import koncurrent.toLater
import symphony.Form
import symphony.toForm
import symphony.toSubmitConfig
import kotlinx.JsExport

class BusinessInfoScene(
    private val config: ProfileScenesConfig<ProfileApiProvider>
) : LazyScene<Form<CorporatePresenter, CorporateOutput, CorporateFields>>() {

    private val api = config.api

    fun initialize() {
        ui.value = Loading("Loading you info. please, wait. . .")
        api.settings().then { settings ->
            settings.map { settings.company.toPresenter() }
        }.then {
            form(it)
        }.finally {
            ui.value = it.toLazyState()
        }
    }
    private fun form(settings: IdentifierSettings<CorporatePresenter>) = CorporateFields(settings.data, settings.country).toForm(
        heading = "Business Info",
        details = "Update your information",
        config = config.toSubmitConfig()
    ) {
        onSubmit { output ->
            output.toLater().then {
                it.toParams().getOrThrow()
            }.andThen {
                api.profile.organisation.update(it)
            }.then {
                it.toPresenter()
            }
        }
    }
}