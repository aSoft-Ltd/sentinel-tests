@file:Suppress("NOTHING_TO_INLINE")

package sentinel

import sentinel.internal.ProfileScenesImpl

inline fun ProfileScenes(config: ProfileScenesConfig<ProfileApiProvider>): ProfileScenes = ProfileScenesImpl(config)