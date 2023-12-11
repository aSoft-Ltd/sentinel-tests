@file:JsExport
@file:Suppress("OPT_IN_USAGE")

package sentinel.fields

import neat.min
import neat.required
import sentinel.AccountType
import symphony.Fields
import symphony.text
import kotlinx.JsExport

class BusinessNameFields internal constructor(
    type: AccountType?, params: BusinessNameOutput
) : Fields<BusinessNameOutput>(params) {
    val name = text(
        name = output::businessName,
        label = "Business name",
        hint = "Peperoni Inc"
    ) {
        if (type == AccountType.Business) {
            min(5)
            required()
        } else {
            optional()
        }
    }
}