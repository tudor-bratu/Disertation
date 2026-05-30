package stud.bratutudor.disertationbe.lint

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import stud.bratutudor.disertationbe.common.LintFinding
import stud.bratutudor.disertationbe.common.Severity
import stud.bratutudor.disertationbe.security.PromptInjectionGuard

@Component
class LintingEngine(
    private val deterministicRules: List<DeterministicRule>,
    private val semanticRules: List<SemanticRule>,
    private val documentParser: DocumentParser,
    private val semanticStage: SemanticStage,
    private val gatekeeping: GatekeepingProperties,
    private val guard: PromptInjectionGuard
) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.info(
            "Linting engine initialised with {} deterministic rule(s) and {} semantic rule(s)",
            deterministicRules.size, semanticRules.size
        )
    }

    fun lint(document: String): LintResult {
        guard.inspect(document)

        val paragraphs = documentParser.parse(document)

        val deterministicFindings = deterministicRules.flatMap { rule ->
            runCatching { rule.apply(paragraphs) }
                .onFailure { log.error("Deterministic rule '{}' threw: {}", rule.ruleId, it.message) }
                .getOrDefault(emptyList())
        }

        val (semanticFindings, status) = runSemanticStage(paragraphs, deterministicFindings)

        val all = (deterministicFindings + semanticFindings).sortedWith(
            compareBy<LintFinding>({ it.location.paragraphIndex }, { it.location.offset })
                .thenByDescending { it.severity }   // most severe first at the same location
                .thenBy { it.ruleId }               // stable tiebreak
        )

        return LintResult(findings = all, semanticStage = status)
    }

    private fun runSemanticStage(
        paragraphs: List<Paragraph>,
        deterministicFindings: List<LintFinding>
    ): Pair<List<LintFinding>, SemanticStageStatus> {
        if (gatekeeping.enabled && shouldGate(deterministicFindings)) {
            log.info(
                "Semantic stage skipped by gatekeeping ({} warning+ findings)",
                deterministicFindings.count { it.severity >= Severity.WARNING })
            return emptyList<LintFinding>() to SemanticStageStatus.SKIPPED_GATEKEEPING
        }
        return when (val r = semanticStage.evaluate(paragraphs)) {
            is SemanticResult.Success -> r.findings to SemanticStageStatus.COMPLETED
            is SemanticResult.Unavailable -> {
                log.info("Semantic stage unavailable: {}", r.reason)
                emptyList<LintFinding>() to SemanticStageStatus.UNAVAILABLE
            }
        }
    }

    private fun shouldGate(findings: List<LintFinding>): Boolean {
        val errors = findings.count { it.severity == Severity.ERROR }
        val warningsOrHigher = findings.count { it.severity >= Severity.WARNING }
        return errors > 0 || warningsOrHigher > gatekeeping.maxWarnings
    }
}

data class LintResult(
    val findings: List<LintFinding>,
    val semanticStage: SemanticStageStatus
)
