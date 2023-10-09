package sentinel

import java.time.LocalDateTime

data class VerificationToken(
    val on: LocalDateTime,
    val to: String,
    val text: String,
)