package stud.bratutudor.disertationbe.augment

import org.springframework.stereotype.Service
import stud.bratutudor.disertationbe.mercury.MercuryAutocompleteRequest
import stud.bratutudor.disertationbe.mercury.MercuryClient
import stud.bratutudor.disertationbe.mercury.MercuryException
import stud.bratutudor.disertationbe.security.PromptInjectionGuard

@Service
class AugmentService(
    private val mercuryClient: MercuryClient,
    private val guard: PromptInjectionGuard
) {
    fun complete(request: AugmentRequest): AugmentResponse {
        guard.inspect(request.prefix, applyKeywordCheck = false)
        guard.inspect(request.suffix, applyKeywordCheck = false)

        val mercuryRequest = MercuryAutocompleteRequest(
            prompt = request.prefix.ifEmpty { EMPTY_DOCUMENT_PROMPT },
            suffix = request.suffix,
            maxTokens = MAX_TOKENS
        )

        val mercuryResponse = mercuryClient.autocomplete(mercuryRequest)

        val completion = mercuryResponse.choices.firstOrNull()?.text ?: throw MercuryException.MalformedResponse(
            "Mercury autocomplete response contained no choices"
        )

        return AugmentResponse(completion)
    }

    private companion object {
        const val MAX_TOKENS = 32
        const val EMPTY_DOCUMENT_PROMPT = " "
    }
}
