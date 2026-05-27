package stud.bratutudor.disertationbe.common

import java.time.Instant

data class ErrorResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val code: String,
    val message: String,
    val fieldErrors: Map<String, String>? = null
)