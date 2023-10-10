package sentinel

import io.ktor.client.*
import kommander.expect
import kommander.expectFailure
import koncurrent.later.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import lexi.ConsoleAppender
import lexi.LogLevel
import lexi.Logger
import sentinel.exceptions.UserAlreadyCompletedRegistrationException
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SignUpParams
import sentinel.params.VerificationParams
import kotlin.test.Test

class RegistrationApiFlixTest {

    private val api: RegistrationApi by lazy {
        val scope = CoroutineScope(SupervisorJob())
        val link = "http://test.com"
        val client = HttpClient {
            developmentMode = true
        }
        val endpoint = RegistrationEndpoint("http://127.0.0.1:8080/api/v1")
        val json = Json { }
        val logger = Logger(ConsoleAppender(level = LogLevel.DEBUG))
        RegistrationApiFlix(RegistrationFlixApiConfig(scope, link, client, logger, endpoint, json))
    }


    @Test
    fun should_be_able_to_begin_the_registration_process() = runTest {
        val res = api.signUp(SignUpParams("Anderson", "andy@lamax.com")).await()
        expect(res.email).toBe("andy@lamax.com")
    }

    @Test
    fun should_fail_to_sign_up_an_already_verified_account() = runTest {
        api.signUp(SignUpParams("Anderson", "andy@lamax.com")).await()
        val exp = expectFailure {
            api.signUp(SignUpParams("Anderson", "andy@lamax.com")).await()
        }
        expect(exp.message).toBe(UserAlreadyCompletedRegistrationException("andy@lamax.com").message)
    }

    @Test
    fun should_be_able_to_send_email_verification_for_a_user_who_has_began_the_registration_process() = runTest {
        val email = "anderson@lamax.com"
        api.signUp(SignUpParams("Anderson", email)).await()
        val res = api.sendVerificationLink(email).await()
        expect(res).toBe(email)
    }

    @Test
    fun should_be_able_to_complete_registration() = runTest {
        val email = "anderson@lamax.com"
        val res = api.signUp(SignUpParams("Anderson", email)).await()
        api.sendVerificationLink(email).await()
        val token = "test-token"
        api.verify(VerificationParams(email = res.email, token = token)).await()
        TODO("figure out how to test this")
    }

    @Test
    fun should_fail_to_send_an_email_verification_for_a_user_who_has_not_began_the_registration_process() = runTest {
        val email = "john.me@yahoo.com"
        val exp = expectFailure { api.sendVerificationLink(email).await() }
        expect(exp.message).toBe(UserDidNotBeginRegistrationException(email).message)
    }
}