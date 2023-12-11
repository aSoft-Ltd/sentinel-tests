@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import kotlinx.JsExport

interface ProfileScenes {
    val business: BusinessProfileScenes
    val personal: PersonalProfileScenes
}