package stud.bratutudor.disertationbe.export

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/export")
class ExportController(
    private val exportService: ExportService
) {

    @PostMapping
    fun export(@Valid @RequestBody exportRequest: ExportRequest): ExportResponse {
        return exportService.export(exportRequest)
    }
}