package com.huskie.languages.dto.user

import java.time.Instant

data class UserResponse(
    val id: Long,
    val email: String,
    val displayName: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
