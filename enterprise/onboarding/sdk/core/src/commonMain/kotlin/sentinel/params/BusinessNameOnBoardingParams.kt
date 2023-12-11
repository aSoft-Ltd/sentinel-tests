@file:JsExport
@file:Suppress("OPT_IN_USAGE")

package sentinel.params

import kotlinx.serialization.Serializable
import kotlinx.JsExport

@Serializable
data class BusinessNameOnBoardingParams(
    var name: String?
)