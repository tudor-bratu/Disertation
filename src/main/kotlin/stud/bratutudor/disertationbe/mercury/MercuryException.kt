package stud.bratutudor.disertationbe.mercury

sealed class MercuryException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    class ApiError(
        val statusCode: Int,
        val responseBody: String?,
        message: String
    ) : MercuryException(message)

    class TransportError(
        message: String,
        cause: Throwable
    ) : MercuryException(message, cause)

    class MalformedResponse(
        message: String,
        cause: Throwable? = null
    ) : MercuryException(message, cause)
}

