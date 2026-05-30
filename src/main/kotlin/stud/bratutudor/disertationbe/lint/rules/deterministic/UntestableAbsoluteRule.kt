package stud.bratutudor.disertationbe.lint.rules.deterministic


import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.LintLocation
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.DeterministicRule
import stud.bratutudor.disertationbe.lint.Paragraph
import stud.bratutudor.disertationbe.lint.Sentences

@Component
class UntestableAbsoluteRule : DeterministicRule {
    override val ruleId = "deterministic.untestable-absolute"

    private val absolutes = Regex(
        "\\b(always|never|all|every|none)\\b",
        RegexOption.IGNORE_CASE
    )

    // a scoping clause anywhere in the same sentence makes the absolute bounded → don't flag
    private val scopingClause = Regex(
        "\\b(during|when|unless|except|while|provided that|as long as)\\b",
        RegexOption.IGNORE_CASE
    )

    override fun apply(paragraphs: List<Paragraph>): List<LintFinding> {
        val findings = mutableListOf<LintFinding>()
        for (p in paragraphs) {
            for (sentence in Sentences.split(p.text)) {
                if (scopingClause.containsMatchIn(sentence.text)) continue   // bounded → skip whole sentence
                for (m in absolutes.findAll(sentence.text)) {
                    // offset within paragraph = sentence offset + match offset within sentence
                    val offsetInParagraph = sentence.offset + m.range.first
                    findings.add(
                        LintFinding(
                            ruleId = ruleId,
                            severity = Severity.WARNING,
                            location = LintLocation(p.index, offsetInParagraph, m.value.length),
                            message = "Unbounded absolute \"${m.value}\" makes the requirement untestable.",
                            suggestion = "Narrow the scope (e.g. \"during normal operation\") or replace " +
                                    "the absolute with a qualified statement that admits the exceptional cases."
                        )
                    )
                }
            }
        }
        return findings
    }
}