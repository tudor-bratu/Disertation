//package stud.bratutudor.disertationbe
//
//import org.junit.jupiter.api.*
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
//import org.springframework.http.MediaType
//import org.springframework.mock.web.MockHttpServletResponse
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.MvcResult
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
//import stud.bratutudor.disertationbe.auth.UserRepository
//import tools.jackson.databind.ObjectMapper
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@DisplayName("Authentication API")
//class AuthControllerIntegrationTest {
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Autowired
//    private lateinit var objectMapper: ObjectMapper
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    @BeforeEach
//    fun setUp() {
//        userRepository.deleteAll()
//    }
//
//    @Nested
//    @DisplayName("POST /api/auth/register")
//    inner class Register {
//
//        @Test
//        @DisplayName("returns 201 and a JWT token for a valid new user")
//        fun `register succeeds with valid input`() {
//            val body = """{"email":"alice@example.com","password":"correct-horse-battery"}"""
//
//            mockMvc.perform(
//                post("/api/auth/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            )
//                .andExpect(status().isCreated)
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.token").isString)
//                .andExpect(jsonPath("$.token").isNotEmpty)
//        }
//
//        @Test
//        @DisplayName("persists the user with a hashed password")
//        fun `register persists user with hashed password`() {
//            val body = """{"email":"bob@example.com","password":"correct-horse-battery"}"""
//
//            mockMvc.perform(
//                post("/api/auth/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().isCreated)
//
//            val saved = userRepository.findByEmail("bob@example.com")
//
//            requireNotNull(saved) { "User was not persisted" }
//
//            check(saved.passwordHash != "correct-horse-battery") {
//                "Password must not be stored in plain text"
//            }
//
//            saved.passwordHash?.let {
//                check(it.startsWith("\$2")) {
//                    "Password hash should be a BCrypt hash"
//                }
//            }
//        }
//
//        @Test
//        @DisplayName("normalises the email to lowercase")
//        fun `register normalises email`() {
//            val body = """{"email":"  CarOl@Example.com  ","password":"correct-horse-battery"}"""
//
//            mockMvc.perform(
//                post("/api/auth/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().isCreated)
//
//            check(userRepository.findByEmail("carol@example.com") != null) {
//                "Email should be normalised to lowercase and trimmed"
//            }
//        }
//
//        @Test
//        @DisplayName("rejects a duplicate email")
//        fun `register rejects duplicate email`() {
//            val body = """{"email":"dup@example.com","password":"correct-horse-battery"}"""
//
//            mockMvc.perform(
//                post("/api/auth/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().isCreated)
//
//            mockMvc.perform(
//                post("/api/auth/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().is4xxClientError)
//        }
//
//        @Test
//        @DisplayName("rejects a malformed email")
//        fun `register rejects malformed email`() {
//            val body = """{"email":"not-an-email","password":"correct-horse-battery"}"""
//
//            mockMvc.perform(
//                post("/api/auth/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().is4xxClientError)
//        }
//
//        @Test
//        @DisplayName("rejects a password shorter than 12 characters")
//        fun `register rejects short password`() {
//            val body = """{"email":"shortpw@example.com","password":"short"}"""
//
//            mockMvc.perform(
//                post("/api/auth/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().is4xxClientError)
//        }
//    }
//
//    @Nested
//    @DisplayName("POST /api/auth/login")
//    inner class Login {
//
//        private val email = "login-test@example.com"
//        private val password = "correct-horse-battery"
//
//        @BeforeEach
//        fun seedUser() {
//            registerUser(email, password)
//        }
//
//        @Test
//        @DisplayName("returns 200 and a JWT for valid credentials")
//        fun `login succeeds with valid credentials`() {
//            val body = """{"email":"$email","password":"$password"}"""
//
//            mockMvc.perform(
//                post("/api/auth/login")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            )
//                .andExpect(status().isOk)
//                .andExpect(jsonPath("$.token").isString)
//                .andExpect(jsonPath("$.token").isNotEmpty)
//        }
//
//        @Test
//        @Disabled("Re-enable after sub-phase 3.6 introduces GlobalExceptionHandler")
//        @DisplayName("rejects a wrong password")
//        fun `login rejects wrong password`() {
//            val body = """{"email":"$email","password":"wrong-password-123"}"""
//
//            mockMvc.perform(
//                post("/api/auth/login")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().is4xxClientError)
//        }
//
//        @Test
//        @DisplayName("rejects an unknown email")
//        fun `login rejects unknown email`() {
//            val body = """{"email":"nobody@example.com","password":"$password"}"""
//
//            mockMvc.perform(
//                post("/api/auth/login")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().is4xxClientError)
//        }
//
//        @Test
//        @DisplayName("updates lastLoginAt on successful login")
//        fun `login updates lastLoginAt`() {
//            val before = userRepository.findByEmail(email)!!.lastLoginAt
//
//            check(before == null) {
//                "lastLoginAt should be null before first login"
//            }
//
//            val body = """{"email":"$email","password":"$password"}"""
//
//            mockMvc.perform(
//                post("/api/auth/login")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(body)
//            ).andExpect(status().isOk)
//
//            val after = userRepository.findByEmail(email)!!.lastLoginAt
//
//            check(after != null) {
//                "lastLoginAt should be set after login"
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("GET /api/auth/me")
//    inner class Me {
//
//        @Test
//        @DisplayName("returns 401 or 403 without a token")
//        fun `me rejects unauthenticated request`() {
//            mockMvc.perform(get("/api/auth/me"))
//                .andExpect(status().is4xxClientError)
//        }
//
//        @Test
//        @DisplayName("returns 401 or 403 with a malformed token")
//        fun `me rejects malformed token`() {
//            mockMvc.perform(
//                get("/api/auth/me")
//                    .header("Authorization", "Bearer this-is-not-a-jwt")
//            ).andExpect(status().is4xxClientError)
//        }
//
//        @Test
//        @DisplayName("returns 401 or 403 with a tampered token")
//        fun `me rejects tampered token`() {
//            val token = registerUser("tamper@example.com", "correct-horse-battery")
//            val tampered = token.dropLast(5) + "AAAAA"
//
//            mockMvc.perform(
//                get("/api/auth/me")
//                    .header("Authorization", "Bearer $tampered")
//            ).andExpect(status().is4xxClientError)
//        }
//
//        @Test
//        @DisplayName("returns 200 and the user id with a valid token")
//        fun `me succeeds with valid token`() {
//            val token = registerUser("me-test@example.com", "correct-horse-battery")
//            val expectedId = userRepository.findByEmail("me-test@example.com")!!.id
//
//            mockMvc.perform(
//                get("/api/auth/me")
//                    .header("Authorization", "Bearer $token")
//            )
//                .andExpect(status().isOk)
//                .andExpect(jsonPath("$.userId").value(expectedId.toString()))
//        }
//    }
//
//    private fun registerUser(email: String, password: String): String {
//        val body = """{"email":"$email","password":"$password"}"""
//
//        val result: MvcResult = mockMvc.perform(
//            post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(body)
//        )
//            .andExpect(status().isCreated)
//            .andReturn()
//
//        val response: MockHttpServletResponse = result.response
//        val json = objectMapper.readTree(response.contentAsString)
//
//        return json.get("token").asText()
//    }
//}