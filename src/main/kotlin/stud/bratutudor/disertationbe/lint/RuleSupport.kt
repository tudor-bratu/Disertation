package stud.bratutudor.disertationbe.lint


import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.LintLocation
import stud.bratutudor.disertationbe.common.Severity

/**
 * Centralises the offset math so each rule declares WHAT to match, not WHERE.
 * Scans every paragraph with [pattern]; for each match, [toTemplate] decides the
 * finding (or returns null to skip). Location is computed automatically as the
 * paragraph index plus the match's offset within that paragraph.
 */
object RuleSupport {
    fun scan(
        paragraphs: List<Paragraph>,
        pattern: Regex,
        toTemplate: (matchText: String) -> FindingTemplate?
    ): List<LintFinding> {
        val findings = mutableListOf<LintFinding>()
        for (p in paragraphs) {
            for (m in pattern.findAll(p.text)) {
                val t = toTemplate(m.value) ?: continue
                findings.add(
                    LintFinding(
                        ruleId = t.ruleId,
                        severity = t.severity,
                        location = LintLocation(p.index, m.range.first, m.value.length),
                        message = t.message,
                        suggestion = t.suggestion
                    )
                )
            }
        }
        return findings
    }
}

data class FindingTemplate(
    val ruleId: String,
    val severity: Severity,
    val message: String,
    val suggestion: String? = null
)