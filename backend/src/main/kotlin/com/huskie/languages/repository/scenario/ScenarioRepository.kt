package com.huskie.languages.repository.scenario

import com.huskie.languages.domain.scenario.LearningLanguage
import com.huskie.languages.domain.scenario.Scenario
import org.springframework.data.jpa.repository.JpaRepository

interface ScenarioRepository : JpaRepository<Scenario, Long> {
    fun existsByLanguage(language: LearningLanguage): Boolean

    fun existsByLanguageAndTitle(language: LearningLanguage, title: String): Boolean
}
