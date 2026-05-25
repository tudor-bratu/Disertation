package stud.bratutudor.disertationbe.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${app.jwt.secret}") private val base64Secret: String,
    @Value("\${app.jwt.expiration-hours}") private val expirationHours: Long
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val signingKey: SecretKey by lazy {
        val keyBytes = Decoders.BASE64.decode(base64Secret)
        require(keyBytes.size >= 32) {
            "JWT secret must decode to at least 32 bytes (256 bits) for HS256"
        }
        Keys.hmacShaKeyFor(keyBytes)
    }

    private val expirationMillis: Long
        get() = expirationHours * 60 * 60 * 1000

    /**
     * Generates a signed JWT for the given user.
     * The user's UUID is stored in the standard `sub` (subject) claim.
     */
    fun generateToken(userId: UUID): String {
        val now = Date()
        val expiry = Date(now.time + expirationMillis)

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey)
            .compact()
    }

    /**
     * Parses and validates a token. Returns the user ID on success, or null on any failure
     * (bad signature, expired, malformed, etc.). Never throws.
     */
    fun extractUserId(token: String): UUID? {
        val claims = parseClaims(token) ?: return null
        return try {
            UUID.fromString(claims.subject)
        } catch (e: IllegalArgumentException) {
            log.warn("JWT subject is not a valid UUID: {}", claims.subject)
            null
        }
    }

    /**
     * Returns true if the token is well-formed, correctly signed, and not expired.
     */
    fun isValid(token: String): Boolean = parseClaims(token) != null

    private fun parseClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            log.debug("JWT expired: {}", e.message)
            null
        } catch (e: JwtException) {
            // Covers SignatureException, MalformedJwtException, UnsupportedJwtException, etc.
            log.debug("JWT invalid: {}", e.message)
            null
        } catch (e: IllegalArgumentException) {
            // Thrown if token is null/empty
            log.debug("JWT argument invalid: {}", e.message)
            null
        }
    }
}