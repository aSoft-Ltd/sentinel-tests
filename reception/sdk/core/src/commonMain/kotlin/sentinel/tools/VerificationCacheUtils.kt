@file:Suppress("NOTHING_TO_INLINE")

package sentinel.tools

import keep.Cache
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams

@PublishedApi
internal const val KEY_VERIFICATION_PARAMS = "sentinel.registration.verification.params"

inline fun Cache.save(params: VerificationParams) = save(KEY_VERIFICATION_PARAMS, params, VerificationParams.serializer())

inline fun Cache.loadVerificationParams() = load(KEY_VERIFICATION_PARAMS, VerificationParams.serializer())

inline fun Cache.removeVerificationParams() = remove(KEY_VERIFICATION_PARAMS)


// -------------------------- Verification Params ---------------------

@PublishedApi
internal const val KEY_ACCOUNT_PARAMS = "sentinel.registration.account.params"


inline fun Cache.save(params: UserAccountParams) = save(KEY_ACCOUNT_PARAMS, params, UserAccountParams.serializer())

inline fun Cache.loadUserAccountParams() = load(KEY_ACCOUNT_PARAMS, UserAccountParams.serializer())

inline fun Cache.removeUserAccountParams() = remove(KEY_ACCOUNT_PARAMS)