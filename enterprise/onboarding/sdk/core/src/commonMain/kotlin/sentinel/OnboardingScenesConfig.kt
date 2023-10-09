package sentinel

import hormone.HasApi
import lexi.Logable

interface OnboardingScenesConfig<out A> : HasApi<A>, Logable