package stud.bratutudor.disertationbe.lint


import org.springframework.stereotype.Component

data class Paragraph(
    val index: Int,
    val text: String,
    val documentOffset: Int   // absolute start of this paragraph's text in the original document
)

@Component
class DocumentParser {

    // A paragraph break is one-or-more blank lines: a line terminator, optional
    // whitespace, then another line terminator. \R matches any Unicode line break.
    private val paragraphDelimiter = Regex("\\R\\s*\\R")

    fun parse(document: String): List<Paragraph> {
        // Build the raw [start, end) spans between delimiters, preserving original offsets.
        val spans = mutableListOf<Pair<Int, Int>>()
        var segmentStart = 0
        for (match in paragraphDelimiter.findAll(document)) {
            spans.add(segmentStart to match.range.first)   // segment ends where the delimiter begins
            segmentStart = match.range.last + 1            // next segment starts after the delimiter
        }
        spans.add(segmentStart to document.length)         // trailing segment

        val paragraphs = mutableListOf<Paragraph>()
        var index = 0
        for ((rawStart, rawEnd) in spans) {
            val raw = document.substring(rawStart, rawEnd)
            val leadingWs = raw.indexOfFirst { !it.isWhitespace() }
            if (leadingWs == -1) continue                  // whitespace-only segment, not a paragraph
            val text = raw.substring(leadingWs).trimEnd()
            // documentOffset points at the first non-whitespace char, so highlighting is accurate
            paragraphs.add(Paragraph(index++, text, rawStart + leadingWs))
        }
        return paragraphs
    }
}