package com.huskie.languages.dto.scenario

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.ScenarioTopic
import java.time.Instant

data class ScenarioResponse(
    val id: Long,
    val title: String,
    val description: String,
    val topic: ScenarioTopic,
    val difficultyLevel: DifficultyLevel,
    val createdAt: Instant
)
