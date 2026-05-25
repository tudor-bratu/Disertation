package stud.bratutudor.disertationbe.auth

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface UserRepository : JpaRepository<User, UUID> {
    fun findUserById(id: UUID): User?
    fun findUserByEmail(email: String): User?
    fun findByEmail(email: String): User?
}