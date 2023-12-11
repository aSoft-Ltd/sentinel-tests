@file:JsExport

package sentinel.params

import kotlinx.serialization.Serializable
import sentinel.AccountType
import kotlinx.JsExport

@Serializable
data class AccountTypeOnBoardingParams(
    var type: AccountType?
)