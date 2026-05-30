package stud.bratutudor.disertationbe.lint.rules.deterministic

import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.DeterministicRule
import stud.bratutudor.disertationbe.lint.FindingTemplate
import stud.bratutudor.disertationbe.lint.Paragraph
import stud.bratutudor.disertationbe.lint.RuleSupport

@Component
class VagueQuantifierRule : DeterministicRule {
    override val ruleId = "deterministic.vague-quantifier"

    private val terms = listOf(
        "fast", "quick", "quickly", "responsive", "slow",          // performance
        "many", "few", "several", "most",                          // capacity
        "user-friendly", "intuitive", "appropriate", "reasonable"  // quality
    )

    private val pattern = Regex(
        "\\b(" + terms.joinToString("|") { Regex.escape(it) } + ")\\b",
        RegexOption.IGNORE_CASE
    )

    override fun apply(paragraphs: List<Paragraph>): List<LintFinding> =
        RuleSupport.scan(paragraphs, pattern) { match ->
            FindingTemplate(
                ruleId = ruleId,
                severity = Severity.WARNING,
                message = "Vague quantifier \"$match\" cannot be evaluated against a test case.",
                suggestion = "Replace \"$match\" with a measurable threshold, an explicit range, " +
                        "or a reference to a documented quality attribute."
            )
        }
}