@file:JsExport
@file:Suppress("OPT_IN_USAGE", "NON_EXPORTABLE_TYPE")

package sentinel.fields

import geo.Country
import neat.required
import symphony.Fields
import symphony.Option
import geo.matches
import symphony.selectSingle
import kotlin.js.JsExport

class CurrencyFields(output: CurrencyOutput) : Fields<CurrencyOutput>(output) {
    val currency = selectSingle(
        name = output::country,
        items = Country.values().sortedBy { it.currency.name },
        mapper = { Option(it.currency.name) },
        filter = { country, key -> country.matches(key) }
    ) { required() }
}