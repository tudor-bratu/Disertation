package stud.bratutudor.disertationbe.auth

import jakarta.security.auth.message.AuthException


sealed class AuthException(message: String) : RuntimeException(message) {
    class EmailAlreadyExists(email: String) : AuthException("Email already registered: $email")
    class InvalidCredentials : AuthException("Invalid email or password")
}