package sentinel

import raven.AddressInfo

class RegistrationEmailConfig(
    val address: AddressInfo,
    val subject: String,
    val template: String
)