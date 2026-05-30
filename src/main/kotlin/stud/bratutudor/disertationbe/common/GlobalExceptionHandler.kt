package stud.bratutudor.disertationbe.common

import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import stud.bratutudor.disertationbe.auth.AuthException
import stud.bratutudor.disertationbe.mercury.MercuryException
import stud.bratutudor.disertationbe.security.GuardException
import java.time.Instant

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler {


    val log = LoggerFactory.getLogger(javaClass)


    @ExceptionHandler(AuthException.EmailAlreadyExists::class)
    fun emailAlreadyExists(ex: AuthException.EmailAlreadyExists): ResponseEntity<ErrorResponse> {
        log.info("Registration rejected: {}", ex.message)

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ErrorResponse(
                    status = 409,
                    code = "EMAIL_ALREADY_EXISTS",
                    message = "An account with this email already exists."
                )
            )
    }

    @ExceptionHandler(AuthException.InvalidCredentials::class)
    fun handleInvalidCredentials(ex: AuthException.InvalidCredentials): ResponseEntity<ErrorResponse> {
        // Deliberately low log level — these are expected on every typo and we don't
        // want noisy logs. But we DO log so brute-force attempts are visible if we
        // grep for them.
        log.info("Login rejected: invalid credentials")
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ErrorResponse(
                    status = 401,
                    code = "INVALID_CREDENTIALS",
                    message = "Invalid email or password."
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "invalid value")
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    code = "VALIDATION_FAILED",
                    message = "Request validation failed.",
                    fieldErrors = fieldErrors
                )
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMalformedJson(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        // Body wasn't valid JSON, or wrong shape entirely.
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    code = "MALFORMED_REQUEST",
                    message = "Request body could not be parsed."
                )
            )
    }

    @ExceptionHandler(GuardException.ContentTooLarge::class)
    fun handleContentTooLarge(ex: GuardException.ContentTooLarge): ResponseEntity<ErrorResponse> {
        log.warn("Content rejected by guard: length exceeded limit {}", ex.limit)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    code = "CONTENT_TOO_LARGE",
                    message = "The submitted content is too large to process."
                )
            )
    }

    @ExceptionHandler(GuardException.SuspiciousContent::class)
    fun handleSuspiciousContent(ex: GuardException.SuspiciousContent): ResponseEntity<ErrorResponse> {
        log.warn("Content rejected by guard: category={}", ex.category)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    code = "SUSPICIOUS_CONTENT",
                    message = "The submitted content was rejected by the content guard."
                )
            )
    }

    @ExceptionHandler(MercuryException.ApiError::class)
    fun handleMercuryApiError(ex: MercuryException.ApiError): ResponseEntity<ErrorResponse> {
        log.error("Mercury API error: status={}, body={}", ex.statusCode, ex.responseBody)
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorResponse(
                timestamp = Instant.now(),
                status = 502,
                code = "UPSTREAM_ERROR",
                message = "Upstream service returned an error"
            )
        )
    }

    @ExceptionHandler(MercuryException.MalformedResponse::class)
    fun handleMalformedResponse(ex: MercuryException.MalformedResponse): ResponseEntity<ErrorResponse> {
        log.error("Mercury returned malformed response: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorResponse(
                timestamp = Instant.now(),
                status = 502,
                code = "UPSTREAM_MALFORMED",
                message = "Upstream service returned an unexpected response"
            )
        )
    }

    @ExceptionHandler(MercuryException.TransportError::class)
    fun handleTransport(ex: MercuryException.TransportError): ResponseEntity<ErrorResponse> {
        log.error("Mercury transport error: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorResponse(
                timestamp = Instant.now(),
                status = 502,
                code = "UPSTREAM_UNAVAILABLE",
                message = "Upstream service is currently unavailable"
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ErrorResponse> {
        // Log the full stack trace SERVER-SIDE only. Never send it to the client.
        log.error("Unhandled exception", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    status = 500,
                    code = "INTERNAL_ERROR",
                    message = "An unexpected error occurred."
                )
            )
    }

}
