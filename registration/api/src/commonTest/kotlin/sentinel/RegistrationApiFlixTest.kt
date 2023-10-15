package sentinel

import io.ktor.client.HttpClient
import kommander.expect
import kommander.expectFailure
import koncurrent.later.await
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import lexi.ConsoleAppender
import lexi.JsonLogFormatter
import lexi.LogLevel
import lexi.Logger
import sentinel.exceptions.InvalidTokenForRegistrationException
import sentinel.exceptions.UserAlreadyBeganRegistrationException
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SignUpParams
import sentinel.params.VerificationParams

class RegistrationApiFlixTest {

    private val api: RegistrationApi by lazy {
        val scope = CoroutineScope(SupervisorJob())
        val link = "http://test.com"
        val client = HttpClient {
            developmentMode = true
        }
        val endpoint = RegistrationEndpoint("http://127.0.0.1:8080/api/v1")
        val json = Json { }
        val logger = Logger(ConsoleAppender(level = LogLevel.DEBUG, formatter = JsonLogFormatter()))
        RegistrationApiFlix(RegistrationFlixApiConfig(scope, link, client, logger, endpoint, json))
    }

    @Test
    fun should_fail_to_sign_up_an_already_verified_account() = runTest {
        api.signUp(SignUpParams("Anderson", "andy@lamax.com")).await()
        val exp = expectFailure {
            api.signUp(SignUpParams("Anderson", "andy@lamax.com")).await()
        }
        expect(exp.message).toBe(UserAlreadyBeganRegistrationException("andy@lamax.com").message)
    }

    @Test
    fun should_fail_to_send_an_email_verification_for_a_user_who_has_not_began_the_registration_process() = runTest {
        val email = "john.me@yahoo.com"
        val exp = expectFailure { api.sendVerificationLink(email).await() }
        expect(exp.message).toBe(UserDidNotBeginRegistrationException(email).message)
    }

    @Test
    fun should_fail_verification_with_an_invalid_token() = runTest {
        val params = VerificationParams(email = "burns@bucky.com", token = "garbage")
        api.signUp(SignUpParams("Bucky Barnes", params.email)).await()
        api.sendVerificationLink(params.email).await()
        val exp = expectFailure { api.verify(params).await() }
        expect(exp.message).toBe(InvalidTokenForRegistrationException(params.token).message)
    }
}