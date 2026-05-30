package stud.bratutudor.disertationbe.lint

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "lint.guard")
data class GuardProperties(
    val enabled: Boolean = true,
    val maxChars: Int = 20_000
)
