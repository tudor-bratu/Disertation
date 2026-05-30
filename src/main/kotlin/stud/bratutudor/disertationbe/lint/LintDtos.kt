package stud.bratutudor.disertationbe.lint

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import stud.bratutudor.disertationbe.common.LintFinding

data class LintRequest(
    @field:NotNull
    @field:Size(max = 50_000, message = "document is too large to lint")
    val document: String
)

data class LintResponse(
    val findings: List<LintFinding>,
    val semanticStageAvailable: Boolean = true
)