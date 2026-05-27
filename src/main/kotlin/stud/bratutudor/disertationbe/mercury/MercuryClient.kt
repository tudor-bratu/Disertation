package stud.bratutudor.disertationbe.mercury


import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class MercuryClient(
    private val mercuryRestClient: RestClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun autocomplete(request: MercuryAutocompleteRequest): MercuryAutocompleteResponse {
        val startMs = System.currentTimeMillis()

        try {
            val response = mercuryRestClient
                .post()
                .uri("/fim/completions")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, clientResponse ->
                    val statusCode = clientResponse.statusCode.value()
                    val body = clientResponse.body.bufferedReader().use { it.readText() }

                    throw MercuryException.ApiError(
                        statusCode = statusCode,
                        responseBody = body,
                        message = "Mercury autocomplete API returned $statusCode"
                    )
                }
                .body(MercuryAutocompleteResponse::class.java)
                ?: throw MercuryException.MalformedResponse(
                    "Mercury autocomplete returned an empty body"
                )

            val elapsedMs = System.currentTimeMillis() - startMs

            log.info(
                "Mercury autocomplete call: model={}, prompt_tokens={}, completion_tokens={}, total_tokens={}, elapsed_ms={}",
                request.model,
                response.usage?.promptTokens,
                response.usage?.completionTokens,
                response.usage?.totalTokens,
                elapsedMs
            )

            return response

        } catch (e: MercuryException) {
            throw e
        } catch (e: ResourceAccessException) {
            throw MercuryException.TransportError(
                message = "Mercury autocomplete network failure: ${e.message}",
                cause = e
            )
        } catch (e: RestClientException) {
            throw MercuryException.TransportError(
                message = "Mercury autocomplete request failed: ${e.message}",
                cause = e
            )
        }
    }

    fun chat(request: MercuryChatRequest): MercuryChatResponse {
        val startMs = System.currentTimeMillis()

        try {
            val response = mercuryRestClient
                .post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, clientResponse ->
                    val statusCode = clientResponse.statusCode.value()
                    val body = clientResponse.body.bufferedReader().use { it.readText() }

                    throw MercuryException.ApiError(
                        statusCode = statusCode,
                        responseBody = body,
                        message = "Mercury chat API returned $statusCode"
                    )
                }
                .body(MercuryChatResponse::class.java)
                ?: throw MercuryException.MalformedResponse(
                    "Mercury chat returned an empty body"
                )

            val elapsedMs = System.currentTimeMillis() - startMs

            log.info(
                "Mercury chat call: model={}, prompt_tokens={}, completion_tokens={}, total_tokens={}, elapsed_ms={}",
                request.model,
                response.usage?.promptTokens,
                response.usage?.completionTokens,
                response.usage?.totalTokens,
                elapsedMs
            )

            return response

        } catch (e: MercuryException) {
            throw e
        } catch (e: ResourceAccessException) {
            throw MercuryException.TransportError(
                message = "Mercury chat network failure: ${e.message}",
                cause = e
            )
        } catch (e: RestClientException) {
            throw MercuryException.TransportError(
                message = "Mercury chat request failed: ${e.message}",
                cause = e
            )
        }
    }
}