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
import lexi.Logger
import raven.AddressInfo
import raven.LocalMemoryMailbox
import raven.MailBox
import raven.MockMailer
import raven.MockMailerConfig
import sentinel.params.SignInParams

class AuthenticationServiceFlixTest {

    private val mailbox: MailBox = LocalMemoryMailbox()

    private val scope = CoroutineScope(SupervisorJob())
    private val client = MongoClient.create("mongodb://root:pass@localhost:27017")
    private val db = client.getDatabase("test-trial")
    private val clock = SystemClock()
    private val mailer = MockMailer(MockMailerConfig(box = mailbox))
    private val logger = Logger(ConsoleAppender())

    private val registration: RegistrationService by lazy {
        val email = RegistrationEmailConfig(
            address = AddressInfo(email = "registration@test.com", name = "Tester"),
            subject = "Please Verify Your Email",
            template = "Hi {{name}}, here is your token {{token}}"
        )
        RegistrationServiceFlix(RegistrationServiceFlixConfig(scope, db, clock, mailer, logger, email))
    }

    val authentication: AuthenticationApi by lazy {
        val email = AuthenticationEmailConfig(
            address = AddressInfo(email = "registration@test.com", name = "Tester"),
            subject = "Please Verify Your Email",
            template = "Hi {{name}}, here is your token {{token}}"
        )
        AuthenticationServiceFlix(AuthenticationServiceFlixConfig(scope,db,clock,mailer,logger,email))
    }

    @Test
    fun should_be_able_to_sign_in_with_a_valid_credential() = runTest {
        val email = "andy@lamax.com"
        registration.register(mailbox, name = "Anderson", email = email, password = email)
        val res = authentication.signIn(SignInParams(email,email)).await()
        expect(res.user.name).toBe("Anderson")
    }
}