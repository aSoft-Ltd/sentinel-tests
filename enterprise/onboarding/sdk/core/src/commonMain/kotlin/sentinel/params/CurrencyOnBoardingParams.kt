@file:JsExport
@file:Suppress("OPT_IN_USAGE")

package sentinel.params

import geo.Country
import kash.Currency
import kotlinx.serialization.Serializable
import kotlinx.JsExport

@Serializable
class CurrencyOnBoardingParams(
    var currency: Currency?
) {
    val country get() = Country.values().find { it.currency == currency }
}