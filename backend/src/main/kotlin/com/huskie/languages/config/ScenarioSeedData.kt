package com.huskie.languages.config

import com.huskie.languages.domain.scenario.LearningLanguage
import com.huskie.languages.domain.scenario.ScenarioTopic

interface ScenarioSeedDataProvider {
    val language: LearningLanguage

    fun scenarios(): List<SeedScenario>
}

data class SeedScenario(
    val title: String,
    val description: String,
    val language: LearningLanguage,
    val topic: ScenarioTopic,
    val lines: List<SeedLine>
)

data class SeedLine(
    val speakerName: String,
    val targetText: String,
    val pronunciationGuide: String,
    val englishTranslation: String,
    val vocabularyItems: List<SeedVocabularyItem>
)

data class SeedVocabularyItem(
    val expression: String,
    val pronunciationGuide: String,
    val gloss: String,
    val explanation: String,
    val startCharIndex: Int,
    val endCharIndex: Int
)

fun seedVocabulary(
    expression: String,
    pronunciationGuide: String,
    gloss: String,
    explanation: String,
    startCharIndex: Int,
    endCharIndex: Int
): SeedVocabularyItem =
    SeedVocabularyItem(
        expression = expression,
        pronunciationGuide = pronunciationGuide,
        gloss = gloss,
        explanation = explanation,
        startCharIndex = startCharIndex,
        endCharIndex = endCharIndex
    )
