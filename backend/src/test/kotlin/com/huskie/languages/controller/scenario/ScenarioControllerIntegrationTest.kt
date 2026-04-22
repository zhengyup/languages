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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import com.huskie.languages.domain.scenario.AudioStatus


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
                    pinyinText = "yì zhí zǒu, rán hòu zuǒ zhuǎn.",
                    englishTranslation = "Go straight, then turn left.",
                    createdAt = Instant.now()
                ),
                ScenarioLine(
                    scenario = scenario,
                    lineOrder = 1,
                    speakerName = "Traveler",
                    hanziText = "请问，地铁站在哪里？",
                    pinyinText = "qǐng wèn, dì tiě zhàn zài nǎ lǐ?",
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
                    pinyin = "qǐng wèn",
                    gloss = "excuse me",
                    explanation = "A polite way to begin a question.",
                    startCharIndex = 0,
                    endCharIndex = 2,
                    createdAt = Instant.now()
                ),
                VocabularyItem(
                    scenarioLine = travelerLine,
                    expression = "地铁站",
                    pinyin = "dì tiě zhàn",
                    gloss = "subway station",
                    explanation = "The destination being asked about.",
                    startCharIndex = 3,
                    endCharIndex = 6,
                    createdAt = Instant.now()
                ),
                VocabularyItem(
                    scenarioLine = travelerLine,
                    expression = "在哪里",
                    pinyin = "zài nǎ lǐ",
                    gloss = "where is it",
                    explanation = "Asks for location.",
                    startCharIndex = 6,
                    endCharIndex = 9,
                    createdAt = Instant.now()
                ),
                VocabularyItem(
                    scenarioLine = localLine,
                    expression = "一直走",
                    pinyin = "yì zhí zǒu",
                    gloss = "go straight",
                    explanation = "Tells someone to continue forward.",
                    startCharIndex = 0,
                    endCharIndex = 3,
                    createdAt = Instant.now()
                ),
                VocabularyItem(
                    scenarioLine = localLine,
                    expression = "然后",
                    pinyin = "rán hòu",
                    gloss = "then",
                    explanation = "Connects the next step in the directions.",
                    startCharIndex = 4,
                    endCharIndex = 6,
                    createdAt = Instant.now()
                ),
                VocabularyItem(
                    scenarioLine = localLine,
                    expression = "左转",
                    pinyin = "zuǒ zhuǎn",
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
            .andExpect(jsonPath("$.lines[0].pinyinText").value("qǐng wèn, dì tiě zhàn zài nǎ lǐ?"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems.length()").value(3))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].expression").value("请问"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].pinyin").value("qǐng wèn"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].gloss").value("excuse me"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].startCharIndex").value(0))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[0].endCharIndex").value(2))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[1].expression").value("地铁站"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[1].pinyin").value("dì tiě zhàn"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[1].startCharIndex").value(3))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[1].endCharIndex").value(6))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[2].expression").value("在哪里"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[2].pinyin").value("zài nǎ lǐ"))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[2].startCharIndex").value(6))
            .andExpect(jsonPath("$.lines[0].vocabularyItems[2].endCharIndex").value(9))
            .andExpect(jsonPath("$.lines[1].lineOrder").value(2))
            .andExpect(jsonPath("$.lines[1].speakerName").value("Local"))
            .andExpect(jsonPath("$.lines[1].hanziText").value("一直走，然后左转。"))
            .andExpect(jsonPath("$.lines[1].pinyinText").value("yì zhí zǒu, rán hòu zuǒ zhuǎn."))
            .andExpect(jsonPath("$.lines[1].vocabularyItems.length()").value(3))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[0].expression").value("一直走"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[0].pinyin").value("yì zhí zǒu"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[0].startCharIndex").value(0))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[0].endCharIndex").value(3))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[1].expression").value("然后"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[1].pinyin").value("rán hòu"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[1].startCharIndex").value(4))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[1].endCharIndex").value(6))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[2].expression").value("左转"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[2].pinyin").value("zuǒ zhuǎn"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[2].gloss").value("turn left"))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[2].startCharIndex").value(6))
            .andExpect(jsonPath("$.lines[1].vocabularyItems[2].endCharIndex").value(8))

        val storedVocabularyItems = vocabularyItemRepository.findAll()
        kotlin.test.assertEquals(6, storedVocabularyItems.size)
        kotlin.test.assertTrue(storedVocabularyItems.any { it.scenarioLine.id == travelerLine.id && it.expression == "请问" })
        kotlin.test.assertTrue(storedVocabularyItems.any { it.scenarioLine.id == localLine.id && it.expression == "左转" })
    }

    @Test
    fun shouldReturnServerErrorWhenScenarioLineVocabularyCoverageIsIncomplete() {
        val scenario = scenarioRepository.save(
            Scenario(
                title = "Restaurant Check-in",
                description = "Practice checking in with the host.",
                topic = ScenarioTopic.RESTAURANT,
                difficultyLevel = DifficultyLevel.BEGINNER,
                createdAt = Instant.now()
            )
        )

        val line = scenarioLineRepository.save(
            ScenarioLine(
                scenario = scenario,
                lineOrder = 1,
                speakerName = "Host",
                hanziText = "请问，几位？",
                pinyinText = "qǐng wèn, jǐ wèi?",
                englishTranslation = "Hello, for how many people?",
                createdAt = Instant.now()
            )
        )

        vocabularyItemRepository.save(
            VocabularyItem(
                scenarioLine = line,
                expression = "请问",
                pinyin = "qǐng wèn",
                gloss = "may I ask",
                explanation = "Polite opener before a question.",
                startCharIndex = 0,
                endCharIndex = 2,
                createdAt = Instant.now()
            )
        )

        mockMvc.perform(get("/scenarios/${scenario.id}"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.message").value("Scenario line 1 does not have complete vocabulary coverage"))
            .andExpect(jsonPath("$.timestamp").isNotEmpty)
    }

    @Test
    fun shouldReturnNotFoundWhenScenarioDoesNotExist() {
        mockMvc.perform(get("/scenarios/999999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Scenario with id 999999 was not found"))
            .andExpect(jsonPath("$.timestamp").isNotEmpty)
    }

    @Test
    fun shouldInvalidateAudioMetadataWhenScenarioLineHanziTextChanges() {
        val scenario = scenarioRepository.save(
            Scenario(
                title = "Audio Update Test",
                description = "Practice updating a line.",
                topic = ScenarioTopic.RESTAURANT,
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                createdAt = Instant.now()
            )
        )

        val line = scenarioLineRepository.save(
            ScenarioLine(
                scenario = scenario,
                lineOrder = 1,
                speakerName = "服务员",
                hanziText = "欢迎光临，请问几位？",
                pinyinText = "huān yíng guāng lín, qǐng wèn jǐ wèi?",
                englishTranslation = "Welcome, how many people?",
                audioUrl = "https://cdn.example.com/audio/line-1.wav",
                audioStatus = AudioStatus.GENERATED,
                audioGeneratedAt = Instant.now(),
                audioSourceTextHash = "original-hash",
                createdAt = Instant.now()
            )
        )

        vocabularyItemRepository.saveAll(
            listOf(
                VocabularyItem(
                    scenarioLine = line,
                    expression = "欢迎光临",
                    pinyin = "huān yíng guāng lín",
                    gloss = "welcome",
                    explanation = "A greeting for customers entering a restaurant.",
                    startCharIndex = 0,
                    endCharIndex = 4,
                    createdAt = Instant.now()
                ),
                VocabularyItem(
                    scenarioLine = line,
                    expression = "请问",
                    pinyin = "qǐng wèn",
                    gloss = "may I ask",
                    explanation = "A polite phrase before asking a question.",
                    startCharIndex = 5,
                    endCharIndex = 7,
                    createdAt = Instant.now()
                ),
                VocabularyItem(
                    scenarioLine = line,
                    expression = "几位",
                    pinyin = "jǐ wèi",
                    gloss = "how many guests",
                    explanation = "Asks for the number of diners.",
                    startCharIndex = 7,
                    endCharIndex = 9,
                    createdAt = Instant.now()
                )
            )
        )

        val requestBody = """
            {
              "speakerName": "服务员",
              "hanziText": "欢迎光临，请问有几位？",
              "pinyinText": "huān yíng guāng lín, qǐng wèn yǒu jǐ wèi?",
              "englishTranslation": "Welcome, may I ask how many guests there are?",
              "vocabularyItems": [
                {
                  "expression": "欢迎光临",
                  "pinyin": "huān yíng guāng lín",
                  "gloss": "welcome",
                  "explanation": "A greeting for customers entering a restaurant.",
                  "startCharIndex": 0,
                  "endCharIndex": 4
                },
                {
                  "expression": "请问",
                  "pinyin": "qǐng wèn",
                  "gloss": "may I ask",
                  "explanation": "A polite phrase before asking a question.",
                  "startCharIndex": 5,
                  "endCharIndex": 7
                },
                {
                  "expression": "有几位",
                  "pinyin": "yǒu jǐ wèi",
                  "gloss": "how many people there are",
                  "explanation": "Asks for the number of diners in a slightly fuller phrasing.",
                  "startCharIndex": 7,
                  "endCharIndex": 10
                }
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            put("/scenarios/${scenario.id}/lines/${line.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(line.id))
            .andExpect(jsonPath("$.hanziText").value("欢迎光临，请问有几位？"))
            .andExpect(jsonPath("$.vocabularyItems.length()").value(3))
            .andExpect(jsonPath("$.vocabularyItems[2].expression").value("有几位"))

        val updatedLine = scenarioLineRepository.findById(line.id!!).orElseThrow()
        kotlin.test.assertEquals("欢迎光临，请问有几位？", updatedLine.hanziText)
        kotlin.test.assertEquals(AudioStatus.PENDING_REGENERATION, updatedLine.audioStatus)
        kotlin.test.assertEquals(null, updatedLine.audioUrl)
        kotlin.test.assertEquals(null, updatedLine.audioGeneratedAt)
        kotlin.test.assertEquals(null, updatedLine.audioSourceTextHash)

        val updatedVocabularyItems = vocabularyItemRepository
            .findAllByScenarioLineIdInOrderByScenarioLineIdAscStartCharIndexAscIdAsc(listOf(line.id!!))
        kotlin.test.assertEquals(3, updatedVocabularyItems.size)
        kotlin.test.assertTrue(updatedVocabularyItems.any { it.expression == "有几位" })
        kotlin.test.assertFalse(updatedVocabularyItems.any { it.expression == "几位" })
    }
}
