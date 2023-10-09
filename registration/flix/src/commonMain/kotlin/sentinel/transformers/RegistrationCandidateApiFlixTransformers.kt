package sentinel.transformers

import raven.AddressInfo
import sentinel.RegistrationCandidate

fun RegistrationCandidate.toAddressInfo() = AddressInfo(
    email = email,
    name = name
)