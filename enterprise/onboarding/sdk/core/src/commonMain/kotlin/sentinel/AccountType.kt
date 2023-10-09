@file:JsExport
@file:Suppress("OPT_IN_USAGE")

package sentinel

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
enum class AccountType {
    Business, Individual
}