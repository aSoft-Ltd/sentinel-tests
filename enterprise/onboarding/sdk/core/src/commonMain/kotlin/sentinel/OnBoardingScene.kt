@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import cinematic.BaseScene
import identifier.CorporatePresenter
import identifier.transformers.toPresenter
import kase.Result
import kase.bagOf
import koncurrent.later.finally
import koncurrent.toLater
import sentinel.fields.AccountTypeFields
import sentinel.fields.AddressFields
import sentinel.fields.BusinessNameFields
import sentinel.fields.CurrencyFields
import symphony.Visibility
import symphony.toForm
import symphony.toSubmitConfig
import kotlin.js.JsExport

class OnBoardingScene(config: OnboardingScenesConfig<ProfileApi>) : BaseScene() {

    private val output = OnBoardingOutput()

    private val af = AddressFields(output)

    val form = listOf(
        OnBoardingStage.Account(
            heading = "Choose your account type",
            details = "Are you an individual or business?",
            fields = AccountTypeFields(output)
        ),
        OnBoardingStage.BusinessName(
            heading = "Enter the name of your business",
            details = "Whats the name of your business?",
            fields = BusinessNameFields(output.type, output)
        ),
        OnBoardingStage.Currency(
            heading = "Choose your default currency",
            details = "Which currency are you operating in?",
            fields = CurrencyFields(output),
            onNext = {
                if (output.address != null) return@Currency
                af.address.country.set(output.country)
            }
        ),
        OnBoardingStage.Address(
            heading = "Enter your operating address",
            details = "Where are you operating from?",
            fields = af
        ),
    ).toForm(
        output = output,
        config = config.toSubmitConfig(),
        visibility = Visibility.Visible
    ) {
        onSubmit { completeOnBoarding() }
    }

    private val api = config.api

    private val completionHandler = bagOf<(Result<CorporatePresenter>) -> Unit>()
    fun initialize(onComplete: (Result<CorporatePresenter>) -> Unit) {
        completionHandler.value = onComplete
    }

    fun deInitialize() {
        completionHandler.clean()
    }

    private fun completeOnBoarding() = output.toLater().then {
        it.toCurrency().getOrThrow()
    }.andThen {
        api.organisation.updateCurrency(it)
    }.then {
        output.toParams().getOrThrow()
    }.andThen {
        api.organisation.update(it)
    }.then {
        it.toPresenter()
    }.finally {
        completionHandler.value?.invoke(it)
    }
}