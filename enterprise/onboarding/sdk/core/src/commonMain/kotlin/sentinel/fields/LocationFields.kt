@file:JsExport
@file:Suppress("OPT_IN_USAGE", "NON_EXPORTABLE_TYPE")

package sentinel.fields

import symphony.Fields
import geo.location
import kotlinx.JsExport

class LocationFields(output: LocationOutput) : Fields<LocationOutput>(output) {
    val location = location(name = output::location)
}