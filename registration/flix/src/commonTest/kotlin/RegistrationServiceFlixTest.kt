import com.mongodb.kotlin.client.coroutine.MongoClient
import kommander.expect
import kommander.expectFailure
import kommander.toContain
import koncurrent.later.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import krono.SystemClock
import lexi.Logger
import raven.AddressInfo
import raven.LocalMemoryMailbox
import raven.MailBox
import raven.MockMailer
import raven.MockMailerConfig
import sentinel.RegistrationEmailConfig
import sentinel.RegistrationService
import sentinel.RegistrationServiceFlix
import sentinel.RegistrationServiceFlixConfig
import sentinel.exceptions.UserAlreadyCompletedRegistrationException
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.VerificationParams
import kotlin.test.Test

class RegistrationServiceFlixTest {

    private val mailbox: MailBox = LocalMemoryMailbox()

    private val emailConfig = RegistrationEmailConfig(
        address = AddressInfo(email = "registration@test.com", name = "Tester"),
        subject = "Please Verify Your Email",
        template = "Hi {{name}}, here is your token {{token}}"
    )

    private val api: RegistrationService by lazy {
        val scope = CoroutineScope(SupervisorJob())
        val client = MongoClient.create("mongodb://root:pass@127.0.0.1:27017/")
        val db = client.getDatabase("test-trial")
        val clock = SystemClock()
        val mailer = MockMailer(MockMailerConfig(box = mailbox))
        val logger = Logger()
        RegistrationServiceFlix(RegistrationServiceFlixConfig(scope, db, clock, mailer, logger, emailConfig))
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
        val res = api.signUp(SignUpParams("Anderson", "anderson@lamax.com")).await()
        val params = SendVerificationLinkParams(email = res.email, link = "https://test.com")
        api.sendVerificationLink(params).await()
        val message = mailbox.load().await().first { msg ->
            msg.to.map { it.email.value }.contains(res.email)
        }
        expect(message.subject).toBe(emailConfig.subject)
        expect(message.body).toContain("Hi Anderson")
    }

    @Test
    fun should_be_able_to_complete_registration() = runTest {
        val res = api.signUp(SignUpParams("Anderson", "anderson@lamax.com")).await()
        val params = SendVerificationLinkParams(email = res.email, link = "https://test.com")
        api.sendVerificationLink(params).await()
        val token = mailbox.load().await().first { msg ->
            msg.to.map { it.email.value }.contains(res.email)
        }.body.split(" ").last()
        api.verify(VerificationParams(email = res.email, token = token)).await()
    }

    @Test
    fun should_fail_to_send_an_email_verification_for_a_user_who_has_not_began_the_registration_process() = runTest {
        val params = SendVerificationLinkParams(email = "juma@yahoo.com", link = "https://test.com")
        val exp = expectFailure { api.sendVerificationLink(params).await() }
        expect(exp.message).toBe(UserDidNotBeginRegistrationException("juma@yahoo.com").message)
    }
}