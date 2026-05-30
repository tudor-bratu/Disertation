package stud.bratutudor.disertationbe.lint.rules.deterministic


import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.LintLocation
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.DeterministicRule
import stud.bratutudor.disertationbe.lint.Paragraph

@Component
class PassiveVoiceWithoutActorRule : DeterministicRule {
    override val ruleId = "deterministic.passive-voice-no-actor"

    private val beVerb = "(?:is|are|was|were|be|been|being)"
    private val irregular = listOf(
        "written", "built", "sent", "made", "held", "kept", "set", "put",
        "shown", "given", "taken", "stored", "thrown", "found", "chosen", "read"
    )
    private val participle = "(?:\\w+ed|" + irregular.joinToString("|") + ")"

    // be-verb, optional adverb (…ly), participle, then capture the rest of the clause
    // so we can check whether an explicit "by <agent>" follows.
    private val pattern = Regex(
        "\\b$beVerb\\s+(?:\\w+ly\\s+)?$participle\\b([^.;]*)",
        RegexOption.IGNORE_CASE
    )
    private val agent = Regex("\\bby\\s+\\w+", RegexOption.IGNORE_CASE)

    override fun apply(paragraphs: List<Paragraph>): List<LintFinding> {
        val findings = mutableListOf<LintFinding>()
        for (p in paragraphs) {
            for (m in pattern.findAll(p.text)) {
                val trailing = m.groupValues[1]
                if (agent.containsMatchIn(trailing)) continue          // agent named → skip
                val passiveLen = m.value.length - trailing.length      // highlight only the verb phrase
                findings.add(
                    LintFinding(
                        ruleId = ruleId,
                        severity = Severity.INFO,
                        location = LintLocation(p.index, m.range.first, passiveLen),
                        message = "Passive construction with no named actor; the responsible component is unspecified.",
                        suggestion = "Rewrite in the active voice, naming the component responsible for the action."
                    )
                )
            }
        }
        return findings
    }
}