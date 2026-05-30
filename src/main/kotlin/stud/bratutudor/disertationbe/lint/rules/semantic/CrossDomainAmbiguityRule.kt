package stud.bratutudor.disertationbe.lint.rules.semantic

import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.lint.SemanticRule

@Component
class CrossDomainAmbiguityRule : SemanticRule {
    override val ruleId = "semantic.cross-domain-ambiguity"
    override val name = "Cross-domain ambiguity"
    override val definition =
        "Flags terms whose meaning depends on the domain they are read in and which the " +
                "document does not disambiguate. The term is correctly spelled and grammatical; the " +
                "ambiguity arises only when it could be read under more than one domain frame."
    override val exampleFindings = listOf(
        "The term \"session\" could mean an authenticated user period, a network channel, or a " +
                "work period; the document does not establish which."
    )
    override val severity = Severity.WARNING
}