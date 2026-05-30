package stud.bratutudor.disertationbe.lint.rules.deterministic

import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.LintLocation
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.DeterministicRule
import stud.bratutudor.disertationbe.lint.Paragraph

@Component
class ModalVerbRule : DeterministicRule {
    override val ruleId = "deterministic.modal-verb-misuse"

    // informal phrasings that smuggle an obligation without an RFC 2119 keyword
    private val informalObligation = Regex(
        "\\b(?:needs to|is expected to|are expected to|is supposed to|has to|have to)\\b",
        RegexOption.IGNORE_CASE
    )

    // a strong modal immediately undercut by a hedge
    private val hedgedModal = Regex(
        "\\b(?:MUST|SHALL)(?:\\s+NOT)?\\b[,\\s]+(?:where possible|if possible|where appropriate|when feasible)",
        RegexOption.IGNORE_CASE
    )

    override fun apply(paragraphs: List<Paragraph>): List<LintFinding> {
        val findings = mutableListOf<LintFinding>()
        for (p in paragraphs) {
            informalObligation.findAll(p.text).forEach { m ->
                findings += finding(
                    p, m,
                    "Informal obligation \"${m.value}\" does not map to an RFC 2119 keyword.",
                    "If binding, use MUST/SHALL; if recommended, SHOULD; if optional, MAY."
                )
            }
            hedgedModal.findAll(p.text).forEach { m ->
                findings += finding(
                    p, m,
                    "A strong obligation is contradicted by a hedge in \"${m.value}\".",
                    "Remove the hedge, or downgrade to SHOULD if deviation is genuinely permitted."
                )
            }
        }
        return findings
    }

    private fun finding(p: Paragraph, m: MatchResult, msg: String, sug: String) =
        LintFinding(
            ruleId, Severity.WARNING,
            LintLocation(p.index, m.range.first, m.value.length), msg, sug
        )
}