package stud.bratutudor.disertationbe.security

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimiter(
    private val props: RateLimitProperties
) {
    private val buckets = ConcurrentHashMap<String, Bucket>()

    fun resolve(userId: String, endpoint: String): Bucket =
        buckets.computeIfAbsent("$userId:$endpoint") { newBucket(endpoint) }

    private fun newBucket(endpoint: String): Bucket {
        val limit = when (endpoint) {
            "augment" -> props.augment
            "export" -> props.export
            else -> props.lint
        }

        val bandwidth = Bandwidth.builder()
            .capacity(limit.capacity)
            .refillGreedy(limit.refillPerMinute, Duration.ofMinutes(1))
            .build()

        return Bucket.builder()
            .addLimit(bandwidth)
            .build()
    }
}
