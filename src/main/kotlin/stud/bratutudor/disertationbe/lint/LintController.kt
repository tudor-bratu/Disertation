package stud.bratutudor.disertationbe.lint

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/lint")
class LintController(
    private val lintService: LintService,
) {

    @PostMapping
    fun lint(@Valid @RequestBody lintRequest: LintRequest): LintResponse =
        lintService.lint(lintRequest)

}
