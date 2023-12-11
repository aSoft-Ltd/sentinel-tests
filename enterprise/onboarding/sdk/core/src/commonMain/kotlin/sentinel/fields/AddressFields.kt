@file:JsExport
@file:Suppress("OPT_IN_USAGE", "NON_EXPORTABLE_TYPE")

package sentinel.fields

import geo.address
import symphony.Fields
import kotlinx.JsExport

class AddressFields(output: OnboardingAddressOutput) : Fields<OnboardingAddressOutput>(output) {
    val address = address(
        name = output::address,
        label = "Business Address"
    )
}