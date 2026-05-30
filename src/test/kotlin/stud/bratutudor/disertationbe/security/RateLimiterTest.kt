package stud.bratutudor.disertationbe.security

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RateLimiterTest {

    @Test
    fun `uses independent buckets for each user and endpoint`() {
        val limiter = RateLimiter(
            RateLimitProperties(
                augment = RateLimitProperties.Limit(capacity = 1, refillPerMinute = 1),
                lint = RateLimitProperties.Limit(capacity = 1, refillPerMinute = 1),
                export = RateLimitProperties.Limit(capacity = 1, refillPerMinute = 1)
            )
        )

        assertTrue(limiter.resolve("user-1", "lint").tryConsume(1))
        assertFalse(limiter.resolve("user-1", "lint").tryConsume(1))

        assertTrue(limiter.resolve("user-1", "augment").tryConsume(1))
        assertTrue(limiter.resolve("user-2", "lint").tryConsume(1))
    }

    @Test
    fun `returns the same bucket for the same user and endpoint`() {
        val limiter = RateLimiter(RateLimitProperties())

        assertSame(
            limiter.resolve("user-1", "lint"),
            limiter.resolve("user-1", "lint")
        )
    }
}
