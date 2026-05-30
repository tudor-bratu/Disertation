package stud.bratutudor.disertationbe.lint.rules.semantic

import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.SemanticRule

@Component
class MissingPreconditionsRule : SemanticRule {
    override val ruleId = "semantic.missing-preconditions"
    override val name = "Missing preconditions"
    override val definition =
        "Flags requirements that describe a behaviour without stating the conditions under " +
                "which it applies — the state the system must be in, or the role the actor must hold — " +
                "leaving the behaviour under-defined."
    override val exampleFindings = listOf(
        "\"shall display the user's transaction history\" omits that the user must be " +
                "authenticated and must have history to display."
    )
    override val severity = Severity.WARNING
}