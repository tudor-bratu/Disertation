package stud.bratutudor.disertationbe.export

import org.springframework.stereotype.Component

@Component
class OpenSpecBriefBuilder {

    fun build(r: ExportRequest): ExportResponse {
        val content = buildString {
            appendLine("# ${r.title.trim()}").appendLine()
            appendLine(r.body.trim()).appendLine()

            if (r.acceptanceCriteria.isNotEmpty()) {
                appendLine("## Acceptance Criteria")
                r.acceptanceCriteria.forEach { appendLine("- ${oneLine(it)}") }
                appendLine()
            }
            if (r.preconditions.isNotEmpty()) {
                appendLine("## Preconditions")
                r.preconditions.forEach { appendLine("- ${oneLine(it)}") }
                appendLine()
            }
            if (r.constraints.isNotEmpty()) {
                appendLine("## Constraints")
                r.constraints.forEach { appendLine("- ${oneLine(it)}") }
                appendLine()
            }
        }.trimEnd() + "\n"

        return ExportResponse(filename = "${slugify(r.title)}.md", content = content)
    }

    private fun slugify(s: String) = s.trim().lowercase()
        .replace(Regex("[^a-z0-9]+"), "-").trim('-').ifBlank { "requirement" }

    private fun oneLine(s: String) = s.trim().replace(Regex("\\s+"), " ")
}