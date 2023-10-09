@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import kotlin.js.JsExport

class PersonalProfileScenes(config: ProfileScenesConfig<ProfileApiProvider>) {
    val picture by lazy { PersonalProfilePictureScene(config) }
    val info by lazy { PersonalProfileInfoScene(config) }
    val security by lazy { ChangePasswordScene(config) }
}