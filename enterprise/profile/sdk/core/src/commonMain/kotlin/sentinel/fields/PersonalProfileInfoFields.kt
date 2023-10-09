@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel.fields

import identifier.IndividualPresenter
import identifier.fields.IndividualOutput
import identifier.transformers.toOutput
import symphony.Fields
import symphony.name
import kotlin.js.JsExport

class PersonalProfileInfoFields(user: IndividualPresenter): Fields<IndividualOutput>(user.toOutput()) {
    val name = name(name = output::name)
}