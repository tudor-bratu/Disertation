package stud.bratutudor.disertationbe.export

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ExportRequest(
    @field:NotBlank @field:Size(max = 500) val title: String,
    @field:NotBlank @field:Size(max = 20_000) val body: String,
    val acceptanceCriteria: List<String> = emptyList(),
    val constraints: List<String> = emptyList(),
    val preconditions: List<String> = emptyList()
)

data class ExportResponse(
    val filename: String,   // e.g. "user-account-closure.md"
    val content: String     // the OpenSpec-ready requirement brief
)