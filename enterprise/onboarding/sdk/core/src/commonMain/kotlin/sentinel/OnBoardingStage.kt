@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import sentinel.fields.AccountTypeFields
import sentinel.fields.AddressFields
import sentinel.fields.BusinessNameFields
import sentinel.fields.CurrencyFields
import sentinel.fields.LocationFields
import symphony.FormStage
import kotlinx.JsExport

sealed class OnBoardingStage : FormStage {
    data class Account(
        override val heading: String,
        override val details: String,
        override val fields: AccountTypeFields
    ) : OnBoardingStage()

    data class BusinessName(
        override val heading: String,
        override val details: String,
        override val fields: BusinessNameFields
    ) : OnBoardingStage()

    data class Currency(
        override val heading: String,
        override val details: String,
        override val fields: CurrencyFields,
        override val onNext: () -> Unit
    ) : OnBoardingStage()

    data class Address(
        override val heading: String,
        override val details: String,
        override val fields: AddressFields
    ) : OnBoardingStage()

    val asAccount get() = this as? Account
    val asBusinessName get() = this as? BusinessName
    val asCurrency get() = this as? Currency
    val asAddress get() = this as? Address
}