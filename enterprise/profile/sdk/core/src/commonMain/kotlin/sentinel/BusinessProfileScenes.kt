@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import kotlinx.JsExport

class BusinessProfileScenes(config: ProfileScenesConfig<ProfileApiProvider>) {
    val logo by lazy { BusinessLogoScene(config) }
    val info by lazy { BusinessInfoScene(config) }
}