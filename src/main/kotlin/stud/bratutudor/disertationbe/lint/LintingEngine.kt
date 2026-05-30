package stud.bratutudor.disertationbe.lint

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding

@Component
class LintingEngine(
    private val deterministicRules: List<DeterministicRule>,
    private val semanticRules: List<SemanticRule>,
    private val documentParser: DocumentParser,
    private val semanticStage: SemanticStage          // NEW
) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.info(
            "Linting engine initialised with {} deterministic rule(s) and {} semantic rule(s)",
            deterministicRules.size, semanticRules.size
        )
    }

    fun lint(document: String): LintResult {
        val paragraphs = documentParser.parse(document)

        val deterministicFindings = deterministicRules.flatMap { rule ->
            runCatching { rule.apply(paragraphs) }
                .onFailure { log.error("Deterministic rule '{}' threw: {}", rule.ruleId, it.message) }
                .getOrDefault(emptyList())
        }

        // 6d will gate this on a deterministic severity threshold. For now: always run.
        val semantic = semanticStage.evaluate(paragraphs)
        val semanticFindings = when (semantic) {
            is SemanticResult.Success -> semantic.findings
            is SemanticResult.Unavailable -> {
                log.info("Semantic stage unavailable: {}", semantic.reason)
                emptyList()
            }
        }
        val semanticAvailable = semantic is SemanticResult.Success

        val all = (deterministicFindings + semanticFindings)
            .sortedWith(compareBy({ it.location.paragraphIndex }, { it.location.offset }))

        return LintResult(findings = all, semanticStageAvailable = semanticAvailable)
    }
}

data class LintResult(
    val findings: List<LintFinding>,
    val semanticStageAvailable: Boolean
)