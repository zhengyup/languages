package com.huskie.languages.repository.scenario

import com.huskie.languages.domain.scenario.AudioStatus
import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.ScenarioTopic
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

    @AfterEach
    fun tearDown() {
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
                hanziText = "你好，欢迎来到中文情景阅读器。",
                pinyinText = "nǐ hǎo, huān yíng lái dào zhōng wén qíng jǐng yuè dú qì.",
                englishTranslation = "Hello, welcome to the Mandarin Scenario Reader.",
                createdAt = Instant.now()
            )
        )

        val reloadedLine = scenarioLineRepository.findById(savedLine.id!!).orElseThrow()

        assertEquals(AudioStatus.NOT_GENERATED, reloadedLine.audioStatus)
        assertNull(reloadedLine.audioUrl)
        assertNull(reloadedLine.audioGeneratedAt)
        assertNull(reloadedLine.audioSourceTextHash)

        val storedAudioStatus = jdbcTemplate.queryForObject(
            "SELECT audio_status FROM scenario_lines WHERE id = ?",
            String::class.java,
            savedLine.id
        )

        assertEquals("NOT_GENERATED", storedAudioStatus)
    }
}
