@file:JsExport
@file:Suppress("OPT_IN_USAGE")

package sentinel.params

import geo.GeoLocation
import kotlinx.serialization.Serializable
import kotlinx.JsExport

@Serializable
class LocationOnBoardingParams(
    var location: GeoLocation? = null
)