@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import koncurrent.Later
import symphony.ImageViewerUploader
import symphony.ImageViewerUploaderState
import kotlin.js.JsExport

class BusinessLogoScene(
    private val config: ProfileScenesConfig<ProfileApiProvider>
) : ImageViewerUploader by ImageViewerUploader(
    onUpload = { logo ->
        Later(logo).andThen {
            config.api.profile.organisation.updateLogo(logo)
        }.then {
            it.image ?: throw RuntimeException("Failed to upload")
        }
    }
) {
    fun initialize() = config.api.settings().then {
        it.company.image ?: throw IllegalArgumentException("No logo set")
    }.then {
        view(it)
    }
}