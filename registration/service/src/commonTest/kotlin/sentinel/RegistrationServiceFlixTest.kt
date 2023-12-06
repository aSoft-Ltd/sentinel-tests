package sentinel

import com.mongodb.kotlin.client.coroutine.MongoClient
import kommander.expect
import kommander.expectFailure
import kommander.toContain
import koncurrent.later.await
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import krono.SystemClock
import lexi.ConsoleAppender
import lexi.ConsoleAppenderOptions
import lexi.JsonLogFormatter
import lexi.loggerFactory
import raven.Address
import raven.BusEmailOptions
import raven.BusEmailReceiver
import raven.BusEmailSender
import raven.BusEmailTopic
import raven.ConsoleEmailSender
import raven.EmailTemplate
import raven.MockEmailSender
import raven.TemplatedEmailOptions
import raven.emailSender
import sanity.LocalBus
import sentinel.exceptions.InvalidTokenForRegistrationException
import sentinel.exceptions.UserAlreadyCompletedRegistrationException
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.VerificationParams

class RegistrationServiceFlixTest {

    private val bus = LocalBus()
    private val topic = BusEmailTopic()
    private val codec = Json { }
    val beo = BusEmailOptions(bus, topic, codec)
    private val receiver = BusEmailReceiver(beo)

    private val mock = MockEmailSender()
    private val domain = "http://sentinel.test"

    private val template = "Hi {{name}}, Welcome to {{brand}}\nClick on this link to verify your token\n{{link}}?token={{token}}"
    private val emailOptions = TemplatedEmailOptions(
        from = Address(email = "registration@test.com", name = "Tester"),
        subject = "Please Verify Your Email",
        template = EmailTemplate(template, template),
        brand = "Sentinel",
        domain = domain,
        address = "Sentinel HQ, Asgard"
    )

    private val service: RegistrationService by lazy {
        val scope = CoroutineScope(SupervisorJob())
        val client = MongoClient.create("mongodb://root:pass@localhost:27017")
        val db = client.getDatabase("test-trial")
        val clock = SystemClock()
        val mailer = emailSender {
            add(ConsoleEmailSender())
            add(BusEmailSender(beo))
            add(mock)
        }
        val logger = loggerFactory {
            add(ConsoleAppender(ConsoleAppenderOptions(formatter = JsonLogFormatter())))
        }
        val database = RegistrationServiceFlixOptions.Database(
            registration = client.getDatabase("registration"),
            authentication = client.getDatabase("authentication")
        )
        RegistrationServiceFlix(RegistrationServiceFlixOptions(scope, database, clock, mailer, logger, emailOptions))
    }

    @Test
    fun should_be_able_to_send_email_verification_for_a_user_who_has_began_the_registration_process() = runTest {
        val res = service.signUp(SignUpParams("Pepper Pots", "pepper@lamax.com")).await()
        val params = SendVerificationLinkParams(email = res.email, link = "https://test.com")
        val email = receiver.anticipate()
        service.sendVerificationLink(params).await()
        val message = email.await()
        expect(message.subject).toBe(emailOptions.subject)
        expect(message.body).toContain("Hi Pepper Pots")
    }

    @Test
    fun should_be_able_to_complete_registration() = runTest {
        val params1 = SignUpParams("Tony Stark", "tony@stark.com")
        val res = service.signUp(params1).await()
        val params2 = SendVerificationLinkParams(email = res.email, link = "https://test.com")

        val email = receiver.anticipate()
        service.sendVerificationLink(params2).await()
        val link = email.await().body.split(" ").last()
        val token = link.split("=").last()

        service.verify(VerificationParams(email = res.email, token = token)).await()

        val exp = expectFailure { service.signUp(params1).await() }
        expect(exp.message).toBe(UserAlreadyCompletedRegistrationException(params1.email).message)
    }

    @Test
    fun should_be_able_to_verify_multiple_times() = runTest {
        val params1 = SignUpParams("Steve Rogers", "steve@rogers.com")
        val res = service.signUp(params1).await()
        val params2 = SendVerificationLinkParams(email = res.email, link = "https://test.com")

        val email = receiver.anticipate()
        service.sendVerificationLink(params2).await()
        val link = email.await().body.split(" ").last()
        val token = link.split("=").last()

        repeat(10) {
            service.verify(VerificationParams(email = res.email, token = token)).await()
        }

        val exp = expectFailure { service.signUp(params1).await() }
        expect(exp.message).toBe(UserAlreadyCompletedRegistrationException(params1.email).message)
    }

    @Test
    fun should_be_able_to_complete_registration_with_any_token() = runTest {
        val params1 = SignUpParams("Jarvis Stark", "jarvis+t2@stark.com")
        val res = service.signUp(params1).await()
        val params2 = SendVerificationLinkParams(email = res.email, link = "https://test.com")

        repeat(10) { service.sendVerificationLink(params2).await() }

        val email = mock.outbox.random()
        val link = email.body.split(" ").last()
        val token = link.split("=").last()

        service.verify(VerificationParams(email = res.email, token = token)).await()

        val exp = expectFailure { service.signUp(params1).await() }
        expect(exp.message).toBe(UserAlreadyCompletedRegistrationException(params1.email).message)
    }

    @Test
    fun should_fail_to_verify_a_rogue_token() = runTest {
        val res = service.signUp(SignUpParams("Wanda Max", "wanda@max.com")).await()
        val params1 = SendVerificationLinkParams(email = res.email, link = "https://test.com")
        service.sendVerificationLink(params1).await()
        val params2 = VerificationParams(email = res.email, token = "garbage")
        val exp = expectFailure { service.verify(params2).await() }
        expect(exp.message).toBe(InvalidTokenForRegistrationException(params2.token).message)
    }

    @Test
    fun should_fail_to_send_an_email_verification_for_a_user_who_has_not_began_the_registration_process() = runTest {
        val params = SendVerificationLinkParams(email = "juma@yahoo.com", link = "https://test.com")
        val exp = expectFailure { service.sendVerificationLink(params).await() }
        expect(exp.message).toBe(UserDidNotBeginRegistrationException("juma@yahoo.com").message)
    }
}