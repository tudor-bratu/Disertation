package stud.bratutudor.disertationbe.lint

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "lint.gatekeeping")
data class GatekeepingProperties(
    val enabled: Boolean = true,
    val maxWarnings: Int = 10   // skip semantic stage if WARNING-or-higher count exceeds this
)