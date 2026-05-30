package stud.bratutudor.disertationbe.lint.rules.deterministic


import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.DeterministicRule
import stud.bratutudor.disertationbe.lint.FindingTemplate
import stud.bratutudor.disertationbe.lint.Paragraph
import stud.bratutudor.disertationbe.lint.RuleSupport

@Component
class WeakObligationRule : DeterministicRule {
    override val ruleId = "deterministic.weak-obligation"

    private val phrases = listOf(
        "should probably", "might want to", "where appropriate",
        "if possible", "as needed", "as appropriate", "if necessary"
        // NOTE: "where appropriate" / "if possible" also appear in ModalVerbRule's
        // hedged-modal check. They co-fire only when following a MUST/SHALL; standalone,
        // only this rule fires. Drop them from one rule if the double-coverage bothers you.
    )

    private val pattern = Regex(
        "\\b(" + phrases.joinToString("|") { Regex.escape(it) } + ")\\b",
        RegexOption.IGNORE_CASE
    )

    override fun apply(paragraphs: List<Paragraph>): List<LintFinding> =
        RuleSupport.scan(paragraphs, pattern) { match ->
            FindingTemplate(
                ruleId = ruleId,
                severity = Severity.WARNING,
                message = "\"$match\" defers obligation without committing to a requirement level.",
                suggestion = "Commit to an obligation strength (MUST/SHOULD/MAY), or move this " +
                        "behaviour into a separate section of optional capabilities."
            )
        }
}