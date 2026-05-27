package stud.bratutudor.disertationbe.mercury

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class MercuryMessage(
    val role: String,
    val content: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MercuryChatRequest(
    val model: String = "mercury-2",
    val messages: List<MercuryMessage>,
    @JsonProperty("max_tokens")
    val maxTokens: Int? = null,
    val temperature: Double? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MercuryAutocompleteRequest(
    val model: String = "mercury-edit-2",
    val prompt: String,
    val suffix: String = ""
)


data class MercuryChatResponse(
    val id: String,
    val created: Long,
    val model: String,
    val choices: List<MercuryChatChoice>,
    val usage: MercuryUsage?
)

data class MercuryChatChoice(
    val index: Int,

    @JsonProperty("finish_reason")
    val finishReason: String?,

    val message: MercuryMessage
)

data class MercuryAutocompleteResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<MercuryAutocompleteChoice>,
    val usage: MercuryUsage?
)

data class MercuryAutocompleteChoice(
    val index: Int,

    @JsonProperty("finish_reason")
    val finishReason: String?,

    val text: String
)

data class MercuryUsage(
    @JsonProperty("prompt_tokens")
    val promptTokens: Int,

    @JsonProperty("completion_tokens")
    val completionTokens: Int,

    @JsonProperty("total_tokens")
    val totalTokens: Int,

    @JsonProperty("reasoning_tokens")
    val reasoningTokens: Int? = null,

    @JsonProperty("cached_input_tokens")
    val cachedInputTokens: Int? = null
)
