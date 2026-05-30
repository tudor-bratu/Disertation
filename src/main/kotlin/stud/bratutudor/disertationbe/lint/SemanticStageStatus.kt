package stud.bratutudor.disertationbe.lint

import com.fasterxml.jackson.annotation.JsonValue

enum class SemanticStageStatus {
    COMPLETED,            // ran and produced findings
    SKIPPED_GATEKEEPING,  // deterministic findings too severe; semantic deliberately not run
    UNAVAILABLE;          // semantic attempted but failed (upstream error / schema rejection)

    @JsonValue
    fun toJson(): String = name.lowercase()
}