package stud.bratutudor.disertationbe.lint.rules.semantic

import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.SemanticRule

@Component
class MisplacedRequirementsRule : SemanticRule {
    override val ruleId = "semantic.misplaced-requirement"
    override val name = "Misplaced (non-functional) requirement"
    override val definition =
        "Flags requirements authored among functional requirements whose subject matter is " +
                "actually non-functional — performance, capacity, security, availability — and which " +
                "belong in a non-functional section."
    override val exampleFindings = listOf(
        "\"shall respond within 200 milliseconds\" is a performance requirement, not a " +
                "behavioural one, and belongs in a non-functional section."
    )
    override val severity = Severity.INFO
}
