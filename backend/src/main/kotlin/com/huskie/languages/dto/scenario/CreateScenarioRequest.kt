package com.huskie.languages.dto.scenario

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.ScenarioTopic
import jakarta.validation.constraints.NotBlank

data class CreateScenarioRequest(
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val description: String,
    val topic: ScenarioTopic,
    val difficultyLevel: DifficultyLevel
)
