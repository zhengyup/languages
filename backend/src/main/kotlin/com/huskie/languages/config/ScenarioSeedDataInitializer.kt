package com.huskie.languages.config

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.VocabularyItem
import com.huskie.languages.repository.scenario.ScenarioLineRepository
import com.huskie.languages.repository.scenario.ScenarioRepository
import com.huskie.languages.repository.scenario.VocabularyItemRepository
import com.huskie.languages.service.scenario.ScenarioLineVocabularyCoverageValidator
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.text.Normalizer
import java.time.Instant

@Component
@Profile("!test")
class ScenarioSeedDataInitializer(
    private val scenarioRepository: ScenarioRepository,
    private val scenarioLineRepository: ScenarioLineRepository,
    private val vocabularyItemRepository: VocabularyItemRepository,
    private val scenarioLineVocabularyCoverageValidator: ScenarioLineVocabularyCoverageValidator,
    private val seedDataProviders: List<ScenarioSeedDataProvider>
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        seedDataProviders
            .sortedBy { it.language.name }
            .forEach(::seedMissingLanguageScenarios)
    }

    private fun seedMissingLanguageScenarios(provider: ScenarioSeedDataProvider) {
        val scenarios = provider.scenarios()
        if (scenarios.isEmpty()) {
            return
        }

        val languageIsSeeded = scenarioRepository.existsByLanguage(provider.language)
        val missingScenarios = scenarios.filterNot {
            scenarioRepository.existsByLanguageAndTitle(provider.language, it.title)
        }

        if (languageIsSeeded.not() || missingScenarios.isNotEmpty()) {
            missingScenarios.forEach(::seedScenario)
        }
    }

    private fun seedScenario(seedScenario: SeedScenario) {
        val createdAt = Instant.now()
        val scenario = scenarioRepository.save(
            Scenario(
                title = seedScenario.title,
                description = seedScenario.description,
                language = seedScenario.language,
                topic = seedScenario.topic,
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                createdAt = createdAt
            )
        )

        seedScenario.lines.forEachIndexed { index, seedLine ->
            val scenarioLine = scenarioLineRepository.save(
                ScenarioLine(
                    scenario = scenario,
                    lineOrder = index + 1,
                    speakerName = seedLine.speakerName,
                    targetText = seedLine.targetText,
                    pronunciationGuide = normalizePronunciationGuide(seedLine.pronunciationGuide),
                    englishTranslation = seedLine.englishTranslation,
                    createdAt = createdAt
                )
            )

            val vocabularyItems = seedLine.vocabularyItems.map { seedVocabularyItem ->
                VocabularyItem(
                    scenarioLine = scenarioLine,
                    expression = seedVocabularyItem.expression,
                    pronunciationGuide = normalizePronunciationGuide(seedVocabularyItem.pronunciationGuide),
                    gloss = seedVocabularyItem.gloss,
                    explanation = seedVocabularyItem.explanation,
                    startCharIndex = seedVocabularyItem.startCharIndex,
                    endCharIndex = seedVocabularyItem.endCharIndex,
                    createdAt = createdAt
                )
            }

            scenarioLineVocabularyCoverageValidator.validate(scenarioLine, vocabularyItems)
            vocabularyItemRepository.saveAll(vocabularyItems)
        }
    }

    private fun normalizePronunciationGuide(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFC)
}
