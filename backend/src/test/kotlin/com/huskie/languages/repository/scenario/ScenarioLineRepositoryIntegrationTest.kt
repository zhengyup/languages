package com.huskie.languages.repository.scenario

import com.huskie.languages.domain.scenario.AudioStatus
import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.LearningLanguage
import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.ScenarioTopic
import com.huskie.languages.domain.scenario.VocabularyItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest
class ScenarioLineRepositoryIntegrationTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var scenarioRepository: ScenarioRepository

    @Autowired
    private lateinit var scenarioLineRepository: ScenarioLineRepository

    @Autowired
    private lateinit var vocabularyItemRepository: VocabularyItemRepository

    @AfterEach
    fun tearDown() {
        vocabularyItemRepository.deleteAll()
        scenarioLineRepository.deleteAll()
        scenarioRepository.deleteAll()
    }

    @Test
    fun shouldPersistDefaultAudioStatusAsNotGenerated() {
        val scenario = scenarioRepository.save(
            Scenario(
                title = "Audio Metadata Test",
                description = "Verifies ScenarioLine audio metadata defaults.",
                topic = ScenarioTopic.RESTAURANT,
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                createdAt = Instant.now()
            )
        )

        val savedLine = scenarioLineRepository.save(
            ScenarioLine(
                scenario = scenario,
                lineOrder = 1,
                speakerName = "Speaker",
                targetText = "你好，欢迎来到中文情景阅读器。",
                pronunciationGuide = "nǐ hǎo, huān yíng lái dào zhōng wén qíng jǐng yuè dú qì.",
                englishTranslation = "Hello, welcome to the Mandarin Scenario Reader.",
                createdAt = Instant.now()
            )
        )

        val reloadedLine = scenarioLineRepository.findById(savedLine.id!!).orElseThrow()

        assertEquals(LearningLanguage.MANDARIN, scenario.language)
        assertEquals(AudioStatus.NOT_GENERATED, reloadedLine.audioStatus)
        assertEquals("你好，欢迎来到中文情景阅读器。", reloadedLine.targetText)
        assertEquals(
            "nǐ hǎo, huān yíng lái dào zhōng wén qíng jǐng yuè dú qì.",
            reloadedLine.pronunciationGuide
        )
        assertNull(reloadedLine.audioUrl)
        assertNull(reloadedLine.audioGeneratedAt)
        assertNull(reloadedLine.audioSourceTextHash)

        val storedAudioStatus = jdbcTemplate.queryForObject(
            "SELECT audio_status FROM scenario_lines WHERE id = ?",
            String::class.java,
            savedLine.id
        )

        assertEquals("NOT_GENERATED", storedAudioStatus)

        val storedScenarioLanguage = jdbcTemplate.queryForObject(
            "SELECT language FROM scenarios WHERE id = ?",
            String::class.java,
            scenario.id
        )
        assertEquals("MANDARIN", storedScenarioLanguage)

        val storedTargetText = jdbcTemplate.queryForObject(
            "SELECT target_text FROM scenario_lines WHERE id = ?",
            String::class.java,
            savedLine.id
        )
        assertEquals("你好，欢迎来到中文情景阅读器。", storedTargetText)

        val storedPronunciationGuide = jdbcTemplate.queryForObject(
            "SELECT pronunciation_guide FROM scenario_lines WHERE id = ?",
            String::class.java,
            savedLine.id
        )
        assertEquals(
            "nǐ hǎo, huān yíng lái dào zhōng wén qíng jǐng yuè dú qì.",
            storedPronunciationGuide
        )
    }

    @Test
    fun shouldPersistVocabularyPronunciationGuide() {
        val scenario = scenarioRepository.save(
            Scenario(
                title = "Vocabulary Metadata Test",
                description = "Verifies vocabulary pronunciation guide persistence.",
                topic = ScenarioTopic.RESTAURANT,
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                createdAt = Instant.now()
            )
        )

        val scenarioLine = scenarioLineRepository.save(
            ScenarioLine(
                scenario = scenario,
                lineOrder = 1,
                speakerName = "Speaker",
                targetText = "请问，几位？",
                pronunciationGuide = "qǐng wèn, jǐ wèi?",
                createdAt = Instant.now()
            )
        )

        val vocabularyItem = vocabularyItemRepository.save(
            VocabularyItem(
                scenarioLine = scenarioLine,
                expression = "请问",
                pronunciationGuide = "qǐng wèn",
                gloss = "may I ask",
                explanation = "A polite phrase before asking a question.",
                startCharIndex = 0,
                endCharIndex = 2,
                createdAt = Instant.now()
            )
        )

        val storedVocabularyPronunciationGuide = jdbcTemplate.queryForObject(
            "SELECT pronunciation_guide FROM vocabulary_items WHERE id = ?",
            String::class.java,
            vocabularyItem.id
        )

        assertEquals("qǐng wèn", vocabularyItem.pronunciationGuide)
        assertEquals("qǐng wèn", storedVocabularyPronunciationGuide)
    }
}
