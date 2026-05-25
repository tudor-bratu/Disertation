package stud.bratutudor.disertationbe.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val normalisedEmail = request.email.trim().lowercase()

        if (userRepository.findByEmail(normalisedEmail) != null) {
            throw AuthException.EmailAlreadyExists(normalisedEmail)
        }

        val user = User(
            email = normalisedEmail,
            passwordHash = passwordEncoder.encode(request.password)
        )
        val saved = userRepository.save(user)

        val token = jwtService.generateToken(saved.id)
        return AuthResponse(token)
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val normalisedEmail = request.email.trim().lowercase()
        val user = userRepository.findByEmail(normalisedEmail)
            ?: throw AuthException.InvalidCredentials()

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw AuthException.InvalidCredentials()
        }

        user.lastLoginAt = Instant.now()
        userRepository.save(user)

        val token = jwtService.generateToken(user.id)
        return AuthResponse(token)
    }
}