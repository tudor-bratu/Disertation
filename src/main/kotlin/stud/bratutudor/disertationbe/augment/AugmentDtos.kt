package stud.bratutudor.disertationbe.augment

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class AugmentRequest(
    @field:NotNull
    @field:Size(max = 4000, message = "prefix must be at most 4000 characters")
    val prefix: String,

    @field:NotNull
    @field:Size(max = 4000, message = "suffix must be at most 4000 characters")
    val suffix: String = ""
)

data class AugmentResponse(
    val completion: String
)
