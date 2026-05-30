package stud.bratutudor.disertationbe.security

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.lint.GuardProperties

@Component
class PromptInjectionGuard(
    private val props: GuardProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val injectionPatterns: List<Regex> = listOf(
        Regex("(?i)ignore\\s+(?:all\\s+|the\\s+)*(?:previous|prior|above)\\s+instructions"),
        Regex("(?i)disregard\\s+(?:all\\s+|the\\s+)*(?:previous|prior|above)"),
        Regex("(?i)forget\\s+(?:everything|all|the\\s+above)"),
        Regex("(?i)you\\s+are\\s+now\\s+(?:a|an|the)\\b"),
        Regex("(?i)\\bact\\s+as\\s+(?:a|an|the)\\b"),
        Regex("(?i)pretend\\s+(?:to\\s+be|you\\s+are)"),
        Regex("(?i)new\\s+instructions\\s*:"),
        Regex("(?im)^\\s*(?:system|assistant|user)\\s*:"),
        Regex("(?i)</?(?:system|instructions?|prompt)>")
    )

    private val zeroWidthAndBidi = setOf(
        '\u200B', '\u200C', '\u200D', '\uFEFF', '\u2060',
        '\u202A', '\u202B', '\u202C', '\u202D', '\u202E',
        '\u2066', '\u2067', '\u2068', '\u2069'
    )

    fun inspect(content: String, applyKeywordCheck: Boolean = true) {
        if (!props.enabled) return

        if (content.length > props.maxChars) {
            log.warn("Guard rejected content: length {} exceeds limit {}", content.length, props.maxChars)
            throw GuardException.ContentTooLarge(props.maxChars)
        }

        if (content.any { it in zeroWidthAndBidi }) {
            log.warn("Guard rejected content: zero-width/bidi control character present")
            throw GuardException.SuspiciousContent("unicode-control")
        }

        if (hasMixedScriptToken(content)) {
            log.warn("Guard rejected content: mixed-script token present")
            throw GuardException.SuspiciousContent("homoglyph")
        }

        if (applyKeywordCheck && injectionPatterns.any { it.containsMatchIn(content) }) {
            log.warn("Guard rejected content: instruction-like pattern matched")
            throw GuardException.SuspiciousContent("instruction-pattern")
        }
    }

    private fun hasMixedScriptToken(content: String): Boolean =
        content.split(Regex("\\s+")).any { token ->
            val hasLatin = token.any { it in 'a'..'z' || it in 'A'..'Z' }
            val hasConfusable = token.any { it in '\u0400'..'\u04FF' || it in '\u0370'..'\u03FF' }
            hasLatin && hasConfusable
        }
}
