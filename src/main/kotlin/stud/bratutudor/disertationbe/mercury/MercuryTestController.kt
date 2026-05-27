package stud.bratutudor.disertationbe.mercury

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/_test/mercury")
class MercuryTestController(
    private val mercuryClient: MercuryClient
) {
    @GetMapping("/ping")
    fun ping(): Map<String, Any?> {
        val request = MercuryChatRequest(
            messages = listOf(
                MercuryMessage(
                    role = "system",
                    content = "You are a JSON producer. Respond with exactly: {\"status\":\"ok\"}"
                ),
                MercuryMessage(role = "user", content = "ping")
            ),
            maxTokens = 50,
            temperature = 0.0
        )

        val response = mercuryClient.chat(request)
        return mapOf(
            "model" to response.model,
            "content" to response.choices.firstOrNull()?.message?.content,
            "tokens" to response.usage?.totalTokens
        )
    }

    // ───────────────────────────────────────────────────────────────
    // 2. Autocomplete (FIM) — simulates the augmentation pillar
    //    The model fills the gap between `prompt` and `suffix`.
    // ───────────────────────────────────────────────────────────────
    @GetMapping("/autocomplete-demo")
    fun autocompleteDemo(): Map<String, Any?> {
        val request = MercuryAutocompleteRequest(
            prompt = "The system MUST authenticate every incoming request before",
            suffix = ". This requirement applies to all API endpoints."
        )
        val response = mercuryClient.autocomplete(request)
        return mapOf(
            "model" to response.model,
            "completion" to response.choices.firstOrNull()?.text,
            "finish_reason" to response.choices.firstOrNull()?.finishReason,
            "tokens" to response.usage?.totalTokens
        )
    }

    // ───────────────────────────────────────────────────────────────
    // 3. Free-form autocomplete via POST — supply your own prompt/suffix
    // ───────────────────────────────────────────────────────────────
    data class AutocompleteTestRequest(
        val prompt: String,
        val suffix: String = ""
    )

    @PostMapping("/autocomplete")
    fun autocomplete(@RequestBody body: AutocompleteTestRequest): Map<String, Any?> {
        val request = MercuryAutocompleteRequest(
            prompt = body.prompt,
            suffix = body.suffix
        )
        val response = mercuryClient.autocomplete(request)
        return mapOf(
            "model" to response.model,
            "completion" to response.choices.firstOrNull()?.text,
            "finish_reason" to response.choices.firstOrNull()?.finishReason,
            "tokens" to response.usage?.totalTokens
        )
    }

    // ───────────────────────────────────────────────────────────────
    // 4. Free-form chat via POST — supply system + user content
    // ───────────────────────────────────────────────────────────────
    data class ChatTestRequest(
        val system: String? = null,
        val user: String,
        val maxTokens: Int? = 200,
        val temperature: Double? = 0.7
    )

    @PostMapping("/chat")
    fun chat(@RequestBody body: ChatTestRequest): Map<String, Any?> {
        val messages = buildList {
            body.system?.let { add(MercuryMessage(role = "system", content = it)) }
            add(MercuryMessage(role = "user", content = body.user))
        }
        val request = MercuryChatRequest(
            messages = messages,
            maxTokens = body.maxTokens,
            temperature = body.temperature
        )
        val response = mercuryClient.chat(request)
        return mapOf(
            "model" to response.model,
            "content" to response.choices.firstOrNull()?.message?.content,
            "finish_reason" to response.choices.firstOrNull()?.finishReason,
            "tokens" to response.usage?.totalTokens
        )
    }

    // ───────────────────────────────────────────────────────────────
    // 5. Linting-style JSON output — forward-looking for Pillar 2
    //    Demonstrates instructing the model to produce structured JSON.
    // ───────────────────────────────────────────────────────────────
    @GetMapping("/lint-demo")
    fun lintDemo(): Map<String, Any?> {
        val sampleRequirement = """
            The system should be fast and support many users.
            It needs to handle errors appropriately.
        """.trimIndent()

        val systemPrompt = """
            You are a requirements linter. Analyse the user's input and respond ONLY with valid JSON
            matching this exact schema, no markdown, no preamble:

            {
              "findings": [
                {
                  "ruleId": "string",
                  "severity": "info" | "warning" | "error",
                  "message": "string",
                  "suggestion": "string"
                }
              ]
            }

            Apply these rules:
            - vague-quantifier: flag words like "fast", "many", "few", "several" without measurable thresholds.
            - weak-obligation: flag "should", "needs to", "is expected to" used as if they were binding obligations.
            - missing-precondition: flag requirements that describe a behaviour without stating when it applies.
        """.trimIndent()

        val request = MercuryChatRequest(
            messages = listOf(
                MercuryMessage(role = "system", content = systemPrompt),
                MercuryMessage(role = "user", content = sampleRequirement)
            ),
            maxTokens = 500,
            temperature = 0.0
        )
        val response = mercuryClient.chat(request)
        return mapOf(
            "model" to response.model,
            "raw_response" to response.choices.firstOrNull()?.message?.content,
            "tokens" to response.usage?.totalTokens
        )
    }
}