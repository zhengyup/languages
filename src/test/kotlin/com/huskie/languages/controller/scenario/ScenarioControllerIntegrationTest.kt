package com.huskie.languages.controller.scenario

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.ScenarioTopic
import com.huskie.languages.repository.scenario.ScenarioRepository
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


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ScenarioControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var scenarioRepository: ScenarioRepository

    @AfterEach
    fun tearDown() {
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
}
