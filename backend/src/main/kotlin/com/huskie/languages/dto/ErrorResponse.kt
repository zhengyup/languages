package com.huskie.languages.dto

import java.time.Instant

data class ErrorResponse(
    val message: String,
    val timestamp: Instant
)
