@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import kotlin.js.JsExport

interface ProfileScenes {
    val business: BusinessProfileScenes
    val personal: PersonalProfileScenes
}