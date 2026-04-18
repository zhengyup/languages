package com.huskie.languages.controller.user

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioTopic
import com.huskie.languages.domain.user.User
import com.huskie.languages.repository.scenario.ScenarioRepository
import com.huskie.languages.repository.user.UserRepository
import com.huskie.languages.repository.user.UserScenarioCompletionRepository
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
class UserScenarioCompletionControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var scenarioRepository: ScenarioRepository

    @Autowired
    private lateinit var userScenarioCompletionRepository: UserScenarioCompletionRepository

    @AfterEach
    fun tearDown() {
        userScenarioCompletionRepository.deleteAll()
        scenarioRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun shouldCreateAndRetrieveScenarioCompletion() {
        val user = userRepository.save(
            User(
                email = "reader@example.com",
                displayName = "Scenario Reader",
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        val scenario = scenarioRepository.save(
            Scenario(
                title = "Ordering Food",
                description = "Restaurant ordering practice.",
                topic = ScenarioTopic.RESTAURANT,
                difficultyLevel = DifficultyLevel.BEGINNER,
                createdAt = Instant.now()
            )
        )

        mockMvc.perform(
            post("/users/${user.id}/scenario-completions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"scenarioId": ${scenario.id}}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.userId").value(user.id))
            .andExpect(jsonPath("$.scenarioId").value(scenario.id))
            .andExpect(jsonPath("$.completedAt").isNotEmpty)

        mockMvc.perform(get("/users/${user.id}/scenario-completions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].userId").value(user.id))
            .andExpect(jsonPath("$[0].scenarioId").value(scenario.id))
            .andExpect(jsonPath("$[0].completedAt").isNotEmpty)
    }

    @Test
    fun shouldRejectDuplicateScenarioCompletion() {
        val user = userRepository.save(
            User(
                email = "duplicate-completion@example.com",
                displayName = "Duplicate Completion User",
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        val scenario = scenarioRepository.save(
            Scenario(
                title = "Directions",
                description = "Asking for directions.",
                topic = ScenarioTopic.DIRECTIONS,
                difficultyLevel = DifficultyLevel.BEGINNER,
                createdAt = Instant.now()
            )
        )

        val requestBody = """{"scenarioId": ${scenario.id}}"""

        mockMvc.perform(
            post("/users/${user.id}/scenario-completions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)

        mockMvc.perform(
            post("/users/${user.id}/scenario-completions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("User ${user.id} has already completed scenario ${scenario.id}"))
            .andExpect(jsonPath("$.timestamp").isNotEmpty)
    }

    @Test
    fun shouldRejectMissingUserOrScenarioReferences() {
        val scenario = scenarioRepository.save(
            Scenario(
                title = "Work Meeting",
                description = "Giving a project update.",
                topic = ScenarioTopic.WORK,
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                createdAt = Instant.now()
            )
        )
        val user = userRepository.save(
            User(
                email = "missing-scenario@example.com",
                displayName = "Missing Scenario User",
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        mockMvc.perform(
            post("/users/999999/scenario-completions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"scenarioId": ${scenario.id}}""")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User with id 999999 was not found"))

        mockMvc.perform(
            post("/users/${user.id}/scenario-completions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"scenarioId": 999999}""")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Scenario with id 999999 was not found"))
    }
}
