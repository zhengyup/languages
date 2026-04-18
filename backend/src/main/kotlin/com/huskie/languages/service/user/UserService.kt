package com.huskie.languages.service.user

import com.huskie.languages.domain.user.User
import com.huskie.languages.dto.user.CreateUserRequest
import com.huskie.languages.dto.user.UserResponse
import com.huskie.languages.exception.user.DuplicateUserEmailException
import com.huskie.languages.exception.user.UserEmailNotFoundException
import com.huskie.languages.exception.user.UserNotFoundException
import com.huskie.languages.repository.user.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(request: CreateUserRequest): UserResponse {
        val normalizedEmail = request.email.trim()
        checkUserExistsByEmail(normalizedEmail)
        val user = generateUser(request)
        return userRepository.save(user).toResponse()
    }

    fun checkUserExistsByEmail(email : String) {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateUserEmailException(email)
        }
    }

    fun generateUser(request : CreateUserRequest) : User {
        val normalizedEmail = request.email.trim()
        val now = Instant.now()
        val user = User(
                email = normalizedEmail,
                displayName = request.displayName.trim(),
                createdAt = now,
                updatedAt = now
        )
        return user
    }

    fun getUserById(id: Long): UserResponse =
        userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }
            .toResponse()

    fun getUserByEmail(email: String): UserResponse =
        userRepository.findByEmail(email.trim())
            .orElseThrow { UserEmailNotFoundException(email.trim()) }
            .toResponse()

    private fun User.toResponse(): UserResponse =
        UserResponse(
            id = checkNotNull(id),
            email = email,
            displayName = displayName,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}
