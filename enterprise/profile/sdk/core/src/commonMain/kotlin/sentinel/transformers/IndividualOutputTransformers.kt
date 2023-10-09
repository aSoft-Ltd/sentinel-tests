package sentinel.transformers

import identifier.fields.IndividualOutput
import identifier.params.IndividualProfileParams
import kase.catching
import neat.required

fun IndividualOutput.toProfileParams() = catching {
    IndividualProfileParams(
        name = this::name.required,
        title = title,
        dob = dob,
        gender = gender,
        idDocumentNumber = idNumber,
        idDocumentType = idType,
        location = location,
        address = address,
    )
}