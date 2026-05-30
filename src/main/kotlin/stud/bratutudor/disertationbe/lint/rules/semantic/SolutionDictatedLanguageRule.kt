package stud.bratutudor.disertationbe.lint.rules.semantic

import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.SemanticRule

@Component
class SolutionDictatedLanguageRule : SemanticRule {
    override val ruleId = "semantic.solution-dictated-language"
    override val name = "Solution-dictated language"
    override val definition =
        "Flags requirements that specify an implementation (a named technology, library, " +
                "framework, or pattern) instead of the behaviour the system must exhibit. Functional " +
                "requirements describe what the system does, not how it does it."
    override val exampleFindings = listOf(
        "\"shall use Redis to cache sessions\" names a technology (Redis) where only the " +
                "behaviour (caching) belongs at the requirements level."
    )
    override val severity = Severity.WARNING
}