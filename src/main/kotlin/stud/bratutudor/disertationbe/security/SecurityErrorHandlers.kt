package stud.bratutudor.disertationbe.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.ErrorResponse
import tools.jackson.databind.ObjectMapper

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        writeError(
            response,
            HttpStatus.UNAUTHORIZED,
            ErrorResponse(
                status = 401,
                code = "AUTHENTICATION_REQUIRED",
                message = "Authentication is required to access this resource."
            ),
            objectMapper
        )
    }
}

@Component
class JwtAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        writeError(
            response,
            HttpStatus.FORBIDDEN,
            ErrorResponse(
                status = 403,
                code = "ACCESS_DENIED",
                message = "You do not have permission to access this resource."
            ),
            objectMapper
        )
    }
}

private fun writeError(
    response: HttpServletResponse,
    status: HttpStatus,
    body: ErrorResponse,
    objectMapper: ObjectMapper
) {
    response.status = status.value()
    response.contentType = MediaType.APPLICATION_JSON_VALUE
    response.characterEncoding = "UTF-8"
    response.writer.write(objectMapper.writeValueAsString(body))
    response.writer.flush()
}