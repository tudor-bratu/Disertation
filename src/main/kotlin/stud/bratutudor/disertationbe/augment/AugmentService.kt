package stud.bratutudor.disertationbe.augment

import org.springframework.stereotype.Service
import stud.bratutudor.disertationbe.mercury.MercuryAutocompleteRequest
import stud.bratutudor.disertationbe.mercury.MercuryClient
import stud.bratutudor.disertationbe.mercury.MercuryException

@Service
class AugmentService(
    private val mercuryClient: MercuryClient
) {
    fun complete(request: AugmentRequest): AugmentResponse {
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
