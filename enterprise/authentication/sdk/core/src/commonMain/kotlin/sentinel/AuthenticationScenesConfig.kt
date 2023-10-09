package sentinel

import hormone.HasApi
import lexi.Logable
import keep.Cacheable
import snitch.Snitch

interface AuthenticationScenesConfig<out A> : HasApi<A>, Logable, Cacheable {
    val toaster: Snitch
}