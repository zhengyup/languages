package com.huskie.languages.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CreateUserRequest(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val displayName: String
)
