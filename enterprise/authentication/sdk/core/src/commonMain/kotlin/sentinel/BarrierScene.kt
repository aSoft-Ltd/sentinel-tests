@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import cinematic.BaseScene
import cinematic.mutableLiveOf
import kase.Failure
import kase.LazyState
import kase.Loading
import kase.Pending
import kase.toLazyState
import koncurrent.Later
import koncurrent.later.finally
import kotlin.js.JsExport

class BarrierScene(config: AuthenticationScenesConfig<AuthenticationApi>) : BaseScene() {
    private val api = config.api

    val ui = mutableLiveOf<LazyState<UserSession>>(Pending)

    fun initialize(onDiscard: (Throwable) -> Unit): Later<UserSession> {
        ui.value = Loading("Attempting to signing you in automatically, please wait . . . ")
        return api.session().finally {
            ui.value = it.toLazyState()
            if (it is Failure) onDiscard(it.cause)
        }
    }

    fun deInitialize() {
        ui.value = Pending
    }
}