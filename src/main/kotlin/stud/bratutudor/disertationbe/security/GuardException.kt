package stud.bratutudor.disertationbe.security

sealed class GuardException(message: String) : RuntimeException(message) {
    class ContentTooLarge(val limit: Int) : GuardException("content exceeds $limit characters")
    class SuspiciousContent(val category: String) : GuardException("content flagged: $category")
}
