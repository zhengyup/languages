package com.huskie.languages.dto.scenario

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.LearningLanguage
import com.huskie.languages.domain.scenario.ScenarioTopic
import java.time.Instant

data class ScenarioDetailResponse(
    val id: Long,
    val title: String,
    val description: String,
    val language: LearningLanguage,
    val topic: ScenarioTopic,
    val difficultyLevel: DifficultyLevel,
    val createdAt: Instant,
    val lines: List<ScenarioLineResponse>
)
