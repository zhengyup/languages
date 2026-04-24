package com.huskie.languages.service.scenario

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.ScenarioTopic
import com.huskie.languages.domain.scenario.VocabularyItem
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertFailsWith

class ScenarioLineVocabularyCoverageValidatorTest {
    private val validator = ScenarioLineVocabularyCoverageValidator()

    @Test
    fun shouldAllowVocabularyItemsThatSpanSpacesWithinPhrase() {
        val line = scenarioLine("Guten Tag, wie kann ich Ihnen helfen?")
        val vocabularyItems = listOf(
            vocabularyItem(line, "Guten Tag", 0, 9),
            vocabularyItem(line, "wie kann ich", 11, 23),
            vocabularyItem(line, "Ihnen helfen", 24, 36)
        )

        validator.validate(line, vocabularyItems)
    }

    @Test
    fun shouldRejectOverlappingVocabularyCoverage() {
        val line = scenarioLine("Guten Tag")
        val vocabularyItems = listOf(
            vocabularyItem(line, "Guten", 0, 5),
            vocabularyItem(line, "Tag", 6, 9),
            vocabularyItem(line, "en T", 3, 7)
        )

        assertFailsWith<Exception> {
            validator.validate(line, vocabularyItems)
        }
    }

    private fun scenarioLine(targetText: String): ScenarioLine {
        val scenario = Scenario(
            id = 1L,
            title = "Test",
            description = "Test scenario",
            topic = ScenarioTopic.TRAVEL,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            createdAt = Instant.now()
        )

        return ScenarioLine(
            id = 1L,
            scenario = scenario,
            lineOrder = 1,
            speakerName = "Speaker",
            targetText = targetText,
            createdAt = Instant.now()
        )
    }

    private fun vocabularyItem(
        line: ScenarioLine,
        expression: String,
        startCharIndex: Int,
        endCharIndex: Int
    ): VocabularyItem =
        VocabularyItem(
            id = startCharIndex.toLong() + 1,
            scenarioLine = line,
            expression = expression,
            pronunciationGuide = expression,
            gloss = expression,
            startCharIndex = startCharIndex,
            endCharIndex = endCharIndex,
            createdAt = Instant.now()
        )
}
