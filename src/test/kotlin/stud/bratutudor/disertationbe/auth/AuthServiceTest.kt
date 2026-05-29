package stud.bratutudor.disertationbe.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.*

/**
 * Pure unit tests for [AuthService].
 *
 * All collaborators (repository, password encoder, JWT service) are mocked, so
 * no Spring context is started, no database is hit, and no real JWT/secret is
 * needed. These run in milliseconds and are safe to execute on every CI run.
 */
class AuthServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var jwtService: JwtService
    private lateinit var authService: AuthService

    @BeforeTest
    fun setUp() {
        userRepository = mockk()
        passwordEncoder = mockk()
        jwtService = mockk()
        authService = AuthService(userRepository, passwordEncoder, jwtService)
    }

    // ---------------------------------------------------------------- register

    @Test
    fun `register normalises the email, hashes the password, and returns a token`() {
        val request = RegisterRequest(email = "  Alice@Example.COM  ", password = "correct-horse-battery")
        val savedUser = slot<User>()

        every { userRepository.findByEmail("alice@example.com") } returns null
        every { passwordEncoder.encode("correct-horse-battery") } returns "hashed-pw"
        every { userRepository.save(capture(savedUser)) } answers { savedUser.captured }
        every { jwtService.generateToken(any()) } returns "jwt-token"

        val response = authService.register(request)

        assertEquals("jwt-token", response.token)
        // Email is trimmed + lowercased before being persisted.
        assertEquals("alice@example.com", savedUser.captured.email)
        // Password is stored hashed, never in plain text.
        assertEquals("hashed-pw", savedUser.captured.passwordHash)
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `register rejects a duplicate email and never saves`() {
        val request = RegisterRequest(email = "Dup@Example.com", password = "correct-horse-battery")
        every { userRepository.findByEmail("dup@example.com") } returns
                User(email = "dup@example.com", passwordHash = "existing-hash")

        assertFailsWith<AuthException.EmailAlreadyExists> {
            authService.register(request)
        }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    // ------------------------------------------------------------------- login

    @Test
    fun `login returns a token and stamps lastLoginAt for valid credentials`() {
        val userId = UUID.randomUUID()
        val user = User(email = "bob@example.com", passwordHash = "hashed-pw", id = userId)
        val request = LoginRequest(email = "  Bob@Example.com ", password = "correct-horse-battery")

        every { userRepository.findByEmail("bob@example.com") } returns user
        every { passwordEncoder.matches("correct-horse-battery", "hashed-pw") } returns true
        every { userRepository.save(any()) } answers { firstArg() }
        every { jwtService.generateToken(userId) } returns "jwt-token"

        val response = authService.login(request)

        assertEquals("jwt-token", response.token)
        assertNotNull(user.lastLoginAt) // was null before, set on successful login
        verify { userRepository.save(user) }
    }

    @Test
    fun `login rejects an unknown email`() {
        val request = LoginRequest(email = "nobody@example.com", password = "correct-horse-battery")
        every { userRepository.findByEmail("nobody@example.com") } returns null

        assertFailsWith<AuthException.InvalidCredentials> {
            authService.login(request)
        }
    }

    @Test
    fun `login rejects a wrong password and never issues a token`() {
        val user = User(email = "carol@example.com", passwordHash = "hashed-pw")
        val request = LoginRequest(email = "carol@example.com", password = "wrong-password")

        every { userRepository.findByEmail("carol@example.com") } returns user
        every { passwordEncoder.matches("wrong-password", "hashed-pw") } returns false

        assertFailsWith<AuthException.InvalidCredentials> {
            authService.login(request)
        }
        verify(exactly = 0) { jwtService.generateToken(any()) }
    }
}