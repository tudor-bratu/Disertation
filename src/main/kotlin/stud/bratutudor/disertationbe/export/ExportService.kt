package stud.bratutudor.disertationbe.export

import org.springframework.stereotype.Service

@Service
class ExportService(
    private val openSpecBriefBuilder: OpenSpecBriefBuilder
) {

    fun export(export: ExportRequest): ExportResponse = openSpecBriefBuilder.build(export)

}