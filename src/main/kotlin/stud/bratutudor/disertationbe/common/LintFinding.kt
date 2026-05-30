package stud.bratutudor.disertationbe.common

import com.fasterxml.jackson.annotation.JsonValue


enum class Severity {
    INFO, WARNING, ERROR;

    @JsonValue
    fun toJson(): String = name.lowercase()
}

data class LintFinding(
    val ruleId: String,            // "deterministic.modal-verb-misuse" / "semantic.cross-domain-ambiguity"
    val severity: Severity,
    val location: LintLocation,
    val message: String,
    val suggestion: String? = null
)

data class LintLocation(
    val paragraphIndex: Int,
    val offset: Int,               // char offset WITHIN the paragraph
    val length: Int
)