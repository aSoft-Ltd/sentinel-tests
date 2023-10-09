package sentinel.internal

import sentinel.BusinessProfileScenes
import sentinel.PersonalProfileScenes
import sentinel.ProfileApiProvider
import sentinel.ProfileScenes
import sentinel.ProfileScenesConfig

@PublishedApi
internal class ProfileScenesImpl(config: ProfileScenesConfig<ProfileApiProvider>) : ProfileScenes {
    override val business by lazy { BusinessProfileScenes(config) }
    override val personal by lazy { PersonalProfileScenes(config) }
}