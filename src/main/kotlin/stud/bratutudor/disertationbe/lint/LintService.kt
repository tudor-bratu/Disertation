package stud.bratutudor.disertationbe.lint

import org.springframework.stereotype.Service

@Service
class LintService(
    private val lintingEngine: LintingEngine
) {


    fun lint(request: LintRequest): LintResponse {
        val result = lintingEngine.lint(request.document)
        return LintResponse(
            result.findings,
            result.semanticStageAvailable
        )
    }
}