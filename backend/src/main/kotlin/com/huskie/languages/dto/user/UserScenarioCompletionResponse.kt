package com.huskie.languages.dto.user

import java.time.Instant

data class UserScenarioCompletionResponse(
    val userId: Long,
    val scenarioId: Long,
    val completedAt: Instant
)
