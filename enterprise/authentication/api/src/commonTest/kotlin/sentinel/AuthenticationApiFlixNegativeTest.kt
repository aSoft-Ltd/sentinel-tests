package sentinel

import io.ktor.client.*
import keep.CacheMock
import kommander.expect
import kommander.expectFailure
import koncurrent.later.await
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import lexi.ConsoleAppender
import lexi.ConsoleAppenderOptions
import lexi.JsonLogFormatter
import lexi.Logger
import lexi.SimpleLogFormatter
import lexi.loggerFactory
import raven.FlixMailBoxOptions
import raven.FlixMailbox
import raven.MailBox
import sentinel.exceptions.UserNotRegisteredForAuthenticationException
import sentinel.params.PasswordResetParams
import sentinel.params.SignInParams

class AuthenticationApiFlixNegativeTest {

    private val scope = CoroutineScope(SupervisorJob())
    private val url = "http://127.0.0.2:8080/api/v1"
    private val client = HttpClient { expectSuccess = false }
    private val logger = loggerFactory {
        add(ConsoleAppender(ConsoleAppenderOptions(formatter = SimpleLogFormatter())))
    }
    private val codec = Json { ignoreUnknownKeys = true }

    val authentication by lazy {
        val options = AuthenticationApiFlixOptions(
            scope = scope,
            link = "http://sentinel.com/recover",
            http = client,
            logger = logger,
            cache = CacheMock(),
            endpoint = AuthenticationEndpoint(url),
            codec = codec
        )
        AuthenticationApiFlix(options)
    }

    @Test
    fun should_fail_to_authenticate_a_none_registered_user() = runTest {
        val email = "petro@yohana.com"
        val exp = expectFailure {
            authentication.signIn(SignInParams(email = email, password = "123456")).await()
        }
        expect(exp.message).toBe(UserNotRegisteredForAuthenticationException(email).message)
    }
}