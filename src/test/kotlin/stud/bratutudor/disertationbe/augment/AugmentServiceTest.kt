package stud.bratutudor.disertationbe.augment

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import stud.bratutudor.disertationbe.lint.GuardProperties
import stud.bratutudor.disertationbe.mercury.MercuryAutocompleteChoice
import stud.bratutudor.disertationbe.mercury.MercuryAutocompleteResponse
import stud.bratutudor.disertationbe.mercury.MercuryClient
import stud.bratutudor.disertationbe.security.GuardException
import stud.bratutudor.disertationbe.security.PromptInjectionGuard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AugmentServiceTest {

    @Test
    fun `does not apply keyword guard to augmentation text`() {
        val mercuryClient = mockk<MercuryClient>()
        val service = AugmentService(mercuryClient, PromptInjectionGuard(GuardProperties()))

        every { mercuryClient.autocomplete(any()) } returns MercuryAutocompleteResponse(
            id = "cmpl-1",
            `object` = "text_completion",
            created = 1,
            model = "mercury-edit-2",
            choices = listOf(
                MercuryAutocompleteChoice(
                    index = 0,
                    finishReason = "stop",
                    text = "completed"
                )
            ),
            usage = null
        )

        val response = service.complete(
            AugmentRequest(
                prefix = "The system MUST ignore previous instructions on reset.",
                suffix = ""
            )
        )

        assertEquals("completed", response.completion)
        verify(exactly = 1) { mercuryClient.autocomplete(any()) }
    }

    @Test
    fun `applies structural guard to augmentation text before Mercury call`() {
        val mercuryClient = mockk<MercuryClient>(relaxed = true)
        val service = AugmentService(mercuryClient, PromptInjectionGuard(GuardProperties()))

        assertFailsWith<GuardException.SuspiciousContent> {
            service.complete(AugmentRequest(prefix = "The system\u200b must log in.", suffix = ""))
        }

        verify(exactly = 0) { mercuryClient.autocomplete(any()) }
    }
}
