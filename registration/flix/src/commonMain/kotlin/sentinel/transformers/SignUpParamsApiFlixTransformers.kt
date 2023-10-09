package sentinel.transformers

import krono.Clock
import krono.currentJavaLocalDateTime
import sentinel.RegistrationCandidate
import sentinel.params.SignUpParams

fun SignUpParams.toDao(clock: Clock) = RegistrationCandidate(
    name = name,
    email = email,
    on = clock.currentJavaLocalDateTime(),
    tokens = emptyList(),
    verified = false
)