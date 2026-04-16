package com.huskie.languages.controller.scenario

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.ScenarioTopic
import com.huskie.languages.domain.scenario.VocabularyItem
import com.huskie.languages.repository.scenario.ScenarioLineRepository
import com.huskie.languages.repository.scenario.ScenarioRepository
import com.huskie.languages.repository.scenario.VocabularyItemRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ScenarioControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

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
    fun shouldCreateAndRetrieveScenarios() {
        val requestBody = """
            {
              "title": "Ordering Food at a Restaurant",
              "description": "Practice a common dining conversation in Mandarin.",
              "topic": "${ScenarioTopic.RESTAURANT}",
              "difficultyLevel": "${DifficultyLevel.BEGINNER}"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/scenarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.title").value("Ordering Food at a Restaurant"))
            .andExpect(jsonPath("$.description").value("Practice a common dining conversation in Mandarin."))
            .andExpect(jsonPath("$.topic").value(ScenarioTopic.RESTAURANT.name))
            .andExpect(jsonPath("$.difficultyLevel").value(DifficultyLevel.BEGINNER.name))
            .andExpect(jsonPath("$.createdAt").isNotEmpty)

        mockMvc.perform(get("/scenarios"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].title").value("Ordering Food at a Restaurant"))
            .andExpect(jsonPath("$[0].description").value("Practice a common dining conversation in Mandarin."))
            .andExpect(jsonPath("$[0].topic").value(ScenarioTopic.RESTAURANT.name))
            .andExpect(jsonPath("$[0].difficultyLevel").value(DifficultyLevel.BEGINNER.name))

        val storedScenario = scenarioRepository.findAll().single()
        kotlin.test.assertEquals("Ordering Food at a Restaurant", storedScenario.title)
        kotlin.test.assertEquals("Practice a common dining conversation in Mandarin.", storedScenario.description)
        kotlin.test.assertEquals(ScenarioTopic.RESTAURANT, storedScenario.topic)
        kotlin.test.assertEquals(DifficultyLevel.BEGINNER, storedScenario.difficultyLevel)
    }

    @Test
    fun shouldRetrieveScenarioDetailWithOrderedLines() {
        val scenario = scenarioRepository.save(
            Scenario(
                title = "Asking for Directions",
                description = "Practice asking for directions in Mandarin.",
                topic = ScenarioTopic.DIRECTIONS,
                difficultyLevel = DifficultyLevel.BEGINNER,
                createdAt = Instant.now()
            )
        )

        val savedLines = scenarioLineRepository.saveAll(
            listOf(
                ScenarioLine(
                    scenario = scenario,
                    lineOrder = 2,
                    speakerName = "Local",
                    hanziText = "一直走，然后左转。",
                    pinyinText = "Yi zhi zou, ran hou zuo zhuan.",
                    englishTranslation = "Go straight, then turn left.",
                    createdAt = Instant.now()
                ),
                ScenarioLine(
                    scenario = scenario,
                    lineOrder = 1,
                    speakerName = "Traveler",
                    hanziText = "请问，地铁站在哪里？",
                    pinyinText = "Qing wen, ditiezhan zai nali?",
                    englishTranslation = "Excuse me, where is the subway station?",
                    createdAt = Instant.now()
                )
            )
        )
        val travelerLine = savedLines.single { it.lineOrder == 1 }
        val localLine = savedLines.single { it.lineOrder == 2 }

        vocabularyItemRepository.saveAll(
            listOf(
                VocabularyItem(
                    scenarioLine = travelerLine,
                    expression = "请问",
                    pinyin = "qing wen",
                    gloss = "excuse me",
                    explanation = "A polite way to begin a question.",
                    startCharIndex = 0,
                    endCharIndex = 2,
                    createdAt = Instant.now()
                ),
                VocabularyItem(
                    scenarioLine = localLine,
                    expression = "左转",
                    pinyin = "zuo zhuan",
                    gloss = "turn left",
                    explanation = "Directional phrase used when guiding someone.",
                    startCharIndex = 6,
                    endCharIndex = 8,
                    createdAt = Instant.now()
                )
            )
        )

        mockMvc.perform(get("/scenarios/${scenario.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(scenario.id))
            .andExpect(jsonPath("$.title").value("Asking for Directions"))
            .andExpect(jsonPath("$.lines.length()").value(2))
            .andExpect(jsonPath("$.lines[0].lineOrder").value(1))
            .andExpect(jsonPath("$.lines[0].speakerName").value("Traveler"))
            .andExpect(jsonPath("$.lines[0].hanziText").value("请问，地铁站在哪里？"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems.length()").value(1))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].expression").value("请问"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].gloss").value("excuse me"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].startCharIndex").value(0))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].endCharIndex").value(2))
            .andExpect(jsonPath("$.lines[1].lineOrder").value(2))
            .andExpect(jsonPath("$.lines[1].speakerName").value("Local"))
            .andExpect(jsonPath("$.lines[1].hanziText").value("一直走，然后左转。"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems.length()").value(1))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[0].expression").value("左转"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[0].gloss").value("turn left"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[0].startCharIndex").value(6))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[0].endCharIndex").value(8))

        val storedVocabularyItems = vocabularyItemRepository.findAll()
        kotlin.test.assertEquals(2, storedVocabularyItems.size)
        kotlin.test.assertTrue(storedVocabularyItems.any { it.scenarioLine.id == travelerLine.id && it.expression == "请问" })
        kotlin.test.assertTrue(storedVocabularyItems.any { it.scenarioLine.id == localLine.id && it.expression == "左转" })
    }

    @Test
    fun shouldReturnNotFoundWhenScenarioDoesNotExist() {
        mockMvc.perform(get("/scenarios/999999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Scenario with id 999999 was not found"))
            .andExpect(jsonPath("$.timestamp").isNotEmpty)
    }
}
