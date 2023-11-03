package sentinel

import com.mongodb.kotlin.client.coroutine.MongoClient
import kommander.expect
import koncurrent.later.await
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import krono.SystemClock
import lexi.ConsoleAppender
import lexi.JsonLogFormatter
import lexi.Logger
import lexi.loggerFactory
import raven.Address
import raven.AddressInfo
import raven.BusEmailReceiver
import raven.BusEmailSender
import raven.BusEmailSenderOptions
import raven.ConsoleEmailSender
import raven.LocalMemoryMailbox
import raven.MailBox
import raven.MockMailer
import raven.MockMailerOptions
import raven.TemplatedEmailOptions
import raven.emailSender
import sanity.LocalBus
import sentinel.params.PasswordResetParams
import sentinel.params.SendPasswordResetParams
import sentinel.params.SignInParams

class AuthenticationServiceFlixTest {

    private val bus = LocalBus()
    private val receiver = BusEmailReceiver(bus)

    private val scope = CoroutineScope(SupervisorJob())
    private val client = MongoClient.create("mongodb://root:pass@localhost:8079")
    private val db = client.getDatabase("test-trial")
    private val clock = SystemClock()
    private val mailer = emailSender {
        add(ConsoleEmailSender())
        add(BusEmailSender(BusEmailSenderOptions(bus)))
    }
    private val logger = loggerFactory {
        add(ConsoleAppender())
    }

    private val registration by lazy {
        val email = TemplatedEmailOptions(
            address = Address(email = "registration@test.com", name = "Tester"),
            subject = "Please Verify Your Email",
            template = "Hi {{name}}, here is your verification token {{token}}"
        )
        RegistrationServiceFlix(RegistrationServiceFlixOptions(scope, db, clock, mailer, logger, email))
    }

    val authentication by lazy {
        val email = TemplatedEmailOptions(
            address = Address(email = "registration@test.com", name = "Tester"),
            subject = "Sentinel Password Recovery",
            template = "Hi {{name}}, here is your recovery token {{token}}"
        )
        AuthenticationServiceFlix(AuthenticationServiceFlixOptions(scope, db, mailer, logger, email))
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
        val session2 = authentication.session(session1.secret).await()
        expect(session1.secret).toBe(session2.secret)
    }

    @Test
    fun should_be_able_to_recover_a_user_password() = runTest {
        val email = "jane@doe.com"
        registration.register(receiver, name = "Jane Doe", email = email, password = email)
        val mail = receiver.anticipate()
        val params = SendPasswordResetParams(email = "jane@doe.com", link = "http://test.com")
        authentication.sendPasswordResetLink(params).await()
        val token = mail.await().body.split(" ").last()
        logger.build().debug("token = $token")
        authentication.resetPassword(PasswordResetParams("new", token)).await()
        authentication.signIn(SignInParams(email, "new")).await()
    }
}