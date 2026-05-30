package stud.bratutudor.disertationbe

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class DisertationBeApplication {
    private val log = LoggerFactory.getLogger(javaClass)

    //    @Bean
//    fun verifyUserRepository(userRepository: UserRepository) = ApplicationRunner {
//
//        val email = "test-${System.currentTimeMillis()}@example.com"
//
//        userRepository.findByEmail(email)?.let { userRepository.delete(it) }
//
//        val user = User(
//
//            email = email,
//
//            passwordHash = "not-a-real-hash-yet"
//
//        )
//
//        val saved = userRepository.save(user)
//
//        log.info("Saved user with id={}, email={}", saved.id, saved.email)
//
//        val fetched = userRepository.findByEmail(email)
//
//        log.info("Fetched user: id={}, createdAt={}", fetched?.id, fetched?.createdAt)
//
//        check(fetched != null) { "Fetched user was null — repository wiring broken" }
//
//        check(fetched.id == saved.id) { "Fetched user id did not match saved id" }
//
//        log.info("✓ Repository round trip succeeded")
//
//    }

//    @Bean
//    fun verifyJwtService(jwtService: JwtService) = ApplicationRunner {
//        val userId = UUID.randomUUID()
//        val token = jwtService.generateToken(userId)
//        log.info("Generated token: {}", token)
//
//        val extracted = jwtService.extractUserId(token)
//        check(extracted == userId) { "Round trip failed: $userId != $extracted" }
//
//        val tampered = token.dropLast(5) + "AAAAA"
//        check(jwtService.extractUserId(tampered) == null) { "Tampered token should not validate" }
//
//        log.info("✓ JwtService round trip succeeded")
//    }
}


fun main(args: Array<String>) {
    runApplication<DisertationBeApplication>(*args)
}
