package stud.bratutudor.disertationbe.lint

import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.Severity

interface DeterministicRule {
    val ruleId: String
    fun apply(paragraphs: List<Paragraph>): List<LintFinding>
}

/** Declarative: contributes to the single batched semantic prompt. Does NOT call the model itself. */
interface SemanticRule {
    val ruleId: String
    val name: String
    val definition: String          // enumerated into the system prompt
    val exampleFindings: List<String>  // one or two illustrative findings, also into the prompt
    val severity: Severity
}
