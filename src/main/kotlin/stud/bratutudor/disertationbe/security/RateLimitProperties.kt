package stud.bratutudor.disertationbe.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ratelimit")
data class RateLimitProperties(
    val augment: Limit = Limit(120, 120),
    val lint: Limit = Limit(30, 30),
    val export: Limit = Limit(10, 10)
) {
    data class Limit(
        val capacity: Long,
        val refillPerMinute: Long
    )
}
