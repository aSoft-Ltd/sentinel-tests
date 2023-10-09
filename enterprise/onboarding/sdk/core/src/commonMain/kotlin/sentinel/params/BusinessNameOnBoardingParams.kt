@file:JsExport
@file:Suppress("OPT_IN_USAGE")

package sentinel.params

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
data class BusinessNameOnBoardingParams(
    var name: String?
)