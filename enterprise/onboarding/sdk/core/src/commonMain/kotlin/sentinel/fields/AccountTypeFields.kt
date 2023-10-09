@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel.fields

import sentinel.AccountType
import symphony.Fields
import symphony.Option
import symphony.selectSingle
import kotlin.js.JsExport

class AccountTypeFields(result: AccountTypeOutput) : Fields<AccountTypeOutput>(result) {
    val type = selectSingle(
        name = output::type,
        label = "Account Type",
        items = AccountType.values().toList(),
        mapper = { Option(it.name, it.name) }
    )
}