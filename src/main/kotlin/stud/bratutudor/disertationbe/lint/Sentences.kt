package stud.bratutudor.disertationbe.lint

import java.text.BreakIterator

data class SentenceSpan(
    val text: String,
    val offset: Int // offset within the paragraph
)

object Sentences {
    fun split(text: String): List<SentenceSpan> {
        val it = BreakIterator.getSentenceInstance()
        it.setText(text)
        val spans = mutableListOf<SentenceSpan>()
        var start = it.first()
        var end = it.next()
        while (end != BreakIterator.DONE) {
            val s = text.substring(start, end)
            if (s.isNotBlank()) spans.add(SentenceSpan(s, start))
            start = end
            end = it.next()
        }
        return spans
    }
}