@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import kotlin.js.JsExport

class AuthenticationScenes(config: AuthenticationScenesConfig<AuthenticationApi>) {
    val signIn by lazy { SignInScene(config) }
    val barrier by lazy { BarrierScene(config) }
    val registration by lazy { RegistrationScene(config) }
    val password by lazy { PasswordScenes(config) }
}