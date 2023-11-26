package sentinel

import io.ktor.client.HttpClient
import keep.CacheMock
import kommander.expect
import koncurrent.later.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import lexi.ConsoleAppender
import lexi.JsonLogFormatter
import lexi.Logger
import lexi.SimpleLogFormatter
import raven.FlixMailBoxOptions
import raven.FlixMailbox
import raven.MailBox
import sentinel.params.PasswordResetParams
import sentinel.params.SignInParams
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import lexi.ConsoleAppenderOptions
import lexi.loggerFactory
import raven.BusEmailReceiver
import raven.setUpConsoleInbox
import sanity.RemoteBus
import sanity.SanityEndpoint

class AuthenticationApiFlixPositiveTest {

    private val url = "http://127.0.0.2:8080/api/v1"
    private val bus = RemoteBus(SanityEndpoint(url))
    private val receiver = BusEmailReceiver(bus)
    private val scope = CoroutineScope(SupervisorJob())
    private val client = HttpClient { expectSuccess = false }
    private val logger = loggerFactory {
        add(ConsoleAppender(ConsoleAppenderOptions(formatter = SimpleLogFormatter())))
    }
    private val codec = Json { ignoreUnknownKeys = true }

    private val registration by lazy {
        val options = RegistrationApiFlixOptions(
            scope = scope,
            link = "http://sentinel.com/verify",
            http = client,
            logger = logger,
            codec = codec,
            endpoint = RegistrationEndpoint(url)
        )
        RegistrationApiFlix(options)
    }

    val authentication by lazy {
        setUpConsoleInbox(bus)
        val options = AuthenticationApiFlixOptions(
            scope = scope,
            link = "http://sentinel.com/recover",
            http = client,
            logger = logger,
            cache = CacheMock(),
            endpoint = AuthenticationEndpoint(url),
            codec = codec,
            sessionCacheKey = "authentication.session"
        )
        AuthenticationApiFlix(options)
    }

    @Test
    fun should_be_able_to_sign_in_with_a_valid_credential() = runTest {
        val email = "andy@lamax.com"
        registration.register(receiver, name = "Anderson", email = email, password = email)
        val res = authentication.signIn(SignInParams(email, email)).await()
        expect(res.user.name).toBe("Anderson")
    }

    @Test
    fun should_be_able_to_request_the_current_session_from_a_token() = runTest {
        val email = "john@doe.com"
        registration.register(receiver, name = "John Doe", email = email, password = email)
        val session1 = authentication.signIn(SignInParams(email, email)).await()
        val session2 = authentication.session().await()
        expect(session1.secret).toBe(session2.secret)
    }

    @Test
    fun should_be_able_to_recover_a_user_password() = runTest(timeout = 30.seconds) {
        val email = "jane@doe.com"
        registration.register(receiver, name = "Jane Doe", email = email, password = email)
        val mail = receiver.anticipate()
        authentication.sendPasswordResetLink("jane@doe.com").await()
        val token = mail.await().body.split(" ").last()
        logger.build().debug("token = $token")
        authentication.resetPassword(PasswordResetParams("new", token)).await()
        authentication.signIn(SignInParams(email, "new")).await()
    }
}