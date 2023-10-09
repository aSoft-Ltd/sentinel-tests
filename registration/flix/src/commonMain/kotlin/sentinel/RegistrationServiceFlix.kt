package sentinel

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import koncurrent.Later
import koncurrent.TODOLater
import koncurrent.later
import koncurrent.later.await
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import krono.currentJavaLocalDateTime
import org.bson.types.ObjectId
import raven.EmailDraft
import sentinel.exceptions.UserAlreadyBeganRegistrationException
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams
import sentinel.transformers.toAddressInfo
import sentinel.transformers.toDao
import yeti.Template

class RegistrationServiceFlix(private val config: RegistrationServiceFlixConfig) : RegistrationService {

    private val col = config.db.getCollection<RegistrationCandidate>("registration.candidates")
    private val mailer = config.mailer
    private val logger by config.logger
    private val actions by lazy { RegistrationActionMessage() }

    override fun signUp(params: SignUpParams) = config.scope.later {
        val action = actions.signUp(params.email)
        logger.info(action.begin)
        val candidates = col.find<SignUpParams>(Filters.eq(RegistrationCandidate::email.name, params.email)).toList()
        if (candidates.isNotEmpty()) {
            throw UserAlreadyBeganRegistrationException(params.email).apply {
                logger.error(action.failed, *arrayOf("reason" to message))
            }
        }
        col.insertOne(params.toDao(config.clock))
        logger.info(action.passed)
        params
    }

    override fun sendVerificationLink(params: SendVerificationLinkParams): Later<String> = config.scope.later {
        val email = params.email
        val link = params.link
        val candidates = col.find(Filters.eq(RegistrationCandidate::email.name, email)).toList()
        if (candidates.isEmpty()) throw UserDidNotBeginRegistrationException(email)
        val candidate = candidates.first()
        val token = ObjectId().toHexString().chunked(4).joinToString("-")
        coroutineScope {
            val updateTask = async {
                val query = Filters.eq(RegistrationCandidate::email.name, email)
                val entry = VerificationToken(
                    on = config.clock.currentJavaLocalDateTime(),
                    to = link,
                    text = token
                )
                val update = Updates.addToSet(RegistrationCandidate::tokens.name, entry)
                col.updateOne(query, update)
            }
            val sendTask = async {
                val message = EmailDraft(
                    subject = config.email.subject,
                    body = Template(config.email.template).compile(
                        "email" to email,
                        "name" to candidate.name,
                        "token" to token
                    )
                )
                mailer.send(draft = message, from = config.email.address, to = candidate.toAddressInfo()).await()
            }
            updateTask.await()
            sendTask.await()
        }
        params.email
    }

    override fun verify(params: VerificationParams): Later<VerificationParams> = TODOLater()

    override fun createUserAccount(params: UserAccountParams): Later<UserAccountParams> = TODOLater()
}