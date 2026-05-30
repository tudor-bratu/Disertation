package stud.bratutudor.disertationbe.lint

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)   // tolerate extra fields rather than crash
data class SemanticModelResponse(
    val findings: List<SemanticModelFinding> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SemanticModelFinding(
    val ruleId: String? = null,
    val paragraphIndex: Int? = null,
    val snippet: String? = null,
    val message: String? = null,
    val suggestion: String? = null
)