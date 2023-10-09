@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import koncurrent.Later
import symphony.ImageViewerUploader
import kotlin.js.JsExport

class PersonalProfilePictureScene(
    private val config: ProfileScenesConfig<ProfileApiProvider>
) : ImageViewerUploader by ImageViewerUploader(
    onUpload = { logo ->
        Later(logo).andThen {
            config.api.profile.personal.changeProfilePicture(it)
        }.then {
            it.image ?: throw RuntimeException("Failed to upload")
        }
    }
) {
    fun initialize() = config.api.settings().then {
        it.user.image ?: throw IllegalArgumentException("No picture set set")
    }.then {
        view(it)
    }

    fun deInitialize() {

    }
}