package stud.bratutudor.disertationbe.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import stud.bratutudor.disertationbe.common.ErrorResponse
import tools.jackson.databind.ObjectMapper
import java.time.Duration

@Component
class RateLimitInterceptor(
    private val rateLimiter: RateLimiter,
    private val objectMapper: ObjectMapper
) : HandlerInterceptor {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val endpoint = endpointFor(request.requestURI) ?: return true
        val userId = currentUserId() ?: return true

        val probe = rateLimiter.resolve(userId, endpoint).tryConsumeAndReturnRemaining(1)
        if (probe.isConsumed) return true

        val retryAfter = Duration.ofNanos(probe.nanosToWaitForRefill).seconds.coerceAtLeast(1)
        log.info("Rate limit hit: user={} endpoint={} retryAfter={}s", userId, endpoint, retryAfter)
        writeTooManyRequests(response, retryAfter)
        return false
    }

    private fun endpointFor(uri: String): String? = when (uri) {
        "/api/augment" -> "augment"
        "/api/lint" -> "lint"
        "/api/export" -> "export"
        else -> null
    }

    private fun currentUserId(): String? =
        SecurityContextHolder.getContext().authentication?.principal
            ?.toString()
            ?.takeIf { it != "anonymousUser" }

    private fun writeTooManyRequests(response: HttpServletResponse, retryAfter: Long) {
        response.status = HttpStatus.TOO_MANY_REQUESTS.value()
        response.setHeader("Retry-After", retryAfter.toString())
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                ErrorResponse(
                    status = 429,
                    code = "RATE_LIMITED",
                    message = "Too many requests. Please slow down and try again shortly."
                )
            )
        )
        response.writer.flush()
    }
}
