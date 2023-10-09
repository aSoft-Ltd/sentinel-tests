package sentinel

import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.routing.Routing
import kase.response.get
import kase.response.post
import koncurrent.later.await
import kotlinx.serialization.StringFormat
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.VerificationParams

fun Routing.installRegistration(service: RegistrationService, endpoint: RegistrationEndpoint, codec: StringFormat) {
    post(endpoint.signUp(), codec) {
        val params = codec.decodeFromString(SignUpParams.serializer(), call.receiveText())
        service.signUp(params).await()
    }

    post(endpoint.sendEmailVerificationLink(), codec) {
        val params = codec.decodeFromString(SendVerificationLinkParams.serializer(), call.receiveText())
        service.sendVerificationLink(params).await()
    }

    post(endpoint.verifyEmail(),codec) {
        val params = codec.decodeFromString(VerificationParams.serializer(),call.receiveText())
        service.verify(params).await()
    }

    post<String>(endpoint.createAccount(),codec) {
        TODO()
    }

    get(endpoint.status(), codec) {
        "Healthy"
    }
}