package com.huskie.languages.controller.user

import com.huskie.languages.repository.user.UserRepository
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
class UserControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @Test
    fun shouldCreateAndRetrieveUser() {
        val requestBody = """
            {
              "email": "learner@example.com",
              "displayName": "Curious Learner"
            }
        """.trimIndent()

        val createResponse = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.email").value("learner@example.com"))
            .andExpect(jsonPath("$.displayName").value("Curious Learner"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty)
            .andExpect(jsonPath("$.updatedAt").isNotEmpty)
            .andReturn()

        val createdUser = userRepository.findAll().single()
        kotlin.test.assertEquals("learner@example.com", createdUser.email)
        kotlin.test.assertEquals("Curious Learner", createdUser.displayName)

        mockMvc.perform(get("/users/${createdUser.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(createdUser.id))
            .andExpect(jsonPath("$.email").value("learner@example.com"))
            .andExpect(jsonPath("$.displayName").value("Curious Learner"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty)
            .andExpect(jsonPath("$.updatedAt").isNotEmpty)
    }

    @Test
    fun shouldRejectDuplicateEmail() {
        val requestBody = """
            {
              "email": "duplicate@example.com",
              "displayName": "First User"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)

        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "email": "duplicate@example.com",
                          "displayName": "Second User"
                        }
                    """.trimIndent()
                )
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("User with email duplicate@example.com already exists"))
            .andExpect(jsonPath("$.timestamp").isNotEmpty)
    }
}
