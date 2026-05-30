package stud.bratutudor.disertationbe.lint

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.mercury.MercuryChatRequest
import stud.bratutudor.disertationbe.mercury.MercuryClient
import stud.bratutudor.disertationbe.mercury.MercuryException
import stud.bratutudor.disertationbe.mercury.MercuryMessage

sealed interface SemanticResult {
    data class Success(val findings: List<LintFinding>) : SemanticResult
    data class Unavailable(val reason: String) : SemanticResult
}

@Component
class SemanticStage(
    private val promptBuilder: SemanticPromptBuilder,
    private val parser: SemanticResponseParser,
    private val mercuryClient: MercuryClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun evaluate(paragraphs: List<Paragraph>): SemanticResult {
        if (paragraphs.isEmpty()) return SemanticResult.Success(emptyList())

        val request = MercuryChatRequest(
            // model defaults to "mercury-2" in your DTO
            messages = listOf(
                MercuryMessage(role = "system", content = promptBuilder.buildSystemPrompt()),
                MercuryMessage(role = "user", content = promptBuilder.buildUserContent(paragraphs))
            ),
            temperature = 0.0          // determinism; we want analysis, not creativity
        )

        val raw = try {
            mercuryClient.chat(request).choices.firstOrNull()?.message?.content
        } catch (e: MercuryException) {
            log.warn("Semantic stage Mercury call failed: {}", e.message)
            return SemanticResult.Unavailable("upstream call failed")
        }

        if (raw.isNullOrBlank()) return SemanticResult.Unavailable("empty model response")

        val findings = parser.parse(raw, paragraphs)
            ?: return SemanticResult.Unavailable("response failed schema validation")

        return SemanticResult.Success(findings)
    }
}