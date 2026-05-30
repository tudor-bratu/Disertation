package stud.bratutudor.disertationbe.augment

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/augment")
class AugmentController(
    private val augmentService: AugmentService
) {
    @PostMapping
    fun augment(@Valid @RequestBody request: AugmentRequest): AugmentResponse =
        augmentService.complete(request)
}
