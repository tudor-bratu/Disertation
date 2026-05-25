package stud.bratutudor.disertationbe.auth

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User {
    @Id
    var id: UUID = UUID.randomUUID()

    @Column(unique = true, nullable = false)
    var email: String = ""

    @Column(nullable = false)
    var passwordHash: String = ""

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()

    var lastLoginAt: Instant? = null
}