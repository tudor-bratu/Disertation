package stud.bratutudor.disertationbe.lint

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.LintLocation
import tools.jackson.databind.ObjectMapper

@Component
class SemanticResponseParser(
    private val objectMapper: ObjectMapper,
    semanticRules: List<SemanticRule>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // ruleId -> severity, for stamping findings; also the allow-list of valid rule ids.
    private val severityByRuleId = semanticRules.associate { it.ruleId to it.severity }

    /**
     * Parse and validate the model's raw text into findings. Returns null if the response
     * cannot be trusted (not parseable, wrong shape) — the caller treats null as
     * "semantic stage unavailable" and falls back to deterministic findings alone.
     */
    fun parse(rawResponse: String, paragraphs: List<Paragraph>): List<LintFinding>? {
        val json = stripFences(rawResponse).trim()

        val parsed = try {
            objectMapper.readValue(json, SemanticModelResponse::class.java)
        } catch (e: Exception) {
            log.warn("Semantic response failed to parse as the expected schema: {}", e.message)
            return null   // reject entirely — do not partially salvage
        }

        val findings = mutableListOf<LintFinding>()
        for (f in parsed.findings) {
            val finding = validateAndConvert(f, paragraphs) ?: continue
            findings.add(finding)
        }
        return findings
    }

    private fun validateAndConvert(f: SemanticModelFinding, paragraphs: List<Paragraph>): LintFinding? {
        val ruleId = f.ruleId
        val severity = ruleId?.let { severityByRuleId[it] }
        if (severity == null) {
            log.warn("Discarding finding with unknown/missing ruleId: {}", ruleId); return null
        }
        val pIndex = f.paragraphIndex
        if (pIndex == null || pIndex !in paragraphs.indices) {
            log.warn("Discarding finding with out-of-range paragraphIndex: {}", pIndex); return null
        }
        if (f.message.isNullOrBlank()) {
            log.warn("Discarding finding with no message (rule {})", ruleId); return null
        }

        val paragraph = paragraphs[pIndex]
        val location = locate(f.snippet, paragraph)

        return LintFinding(
            ruleId = ruleId,
            severity = severity,
            location = location,
            message = f.message,
            suggestion = f.suggestion?.takeIf { it.isNotBlank() && !it.equals("null", true) }
        )
    }

    /** Resolve the model's snippet to a paragraph-relative offset+length. */
    private fun locate(snippet: String?, p: Paragraph): LintLocation {
        if (snippet.isNullOrBlank()) return LintLocation(p.index, 0, p.text.length)

        // exact match first
        val exact = p.text.indexOf(snippet)
        if (exact >= 0) return LintLocation(p.index, exact, snippet.length)

        // whitespace-normalised fallback: the model often reflows internal spacing
        val normSnippet = snippet.replace(Regex("\\s+"), " ").trim()
        val normText = p.text.replace(Regex("\\s+"), " ")
        val normIdx = normText.indexOf(normSnippet)
        if (normIdx >= 0) {
            // best-effort: highlight from the normalised index, clamped to the real length
            val start = normIdx.coerceAtMost(p.text.length)
            val len = normSnippet.length.coerceAtMost(p.text.length - start)
            return LintLocation(p.index, start, len)
        }

        // give up on a precise span — highlight the whole paragraph and log it
        log.debug("Snippet not found in P{}; falling back to whole-paragraph highlight", p.index)
        return LintLocation(p.index, 0, p.text.length)
    }

    /** Defensive: strip ```json fences if the model adds them despite instructions. */
    private fun stripFences(s: String): String =
        s.replace(Regex("(?s)```(?:json)?\\s*(.*?)\\s*```"), "$1")
}