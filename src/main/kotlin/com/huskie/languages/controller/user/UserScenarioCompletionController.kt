package com.huskie.languages.controller.user

import com.huskie.languages.dto.user.CreateUserScenarioCompletionRequest
import com.huskie.languages.dto.user.UserScenarioCompletionResponse
import com.huskie.languages.service.user.UserScenarioCompletionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/{userId}/scenario-completions")
class UserScenarioCompletionController(
    private val userScenarioCompletionService: UserScenarioCompletionService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createScenarioCompletion(
        @PathVariable userId: Long,
        @Valid @RequestBody request: CreateUserScenarioCompletionRequest
    ): UserScenarioCompletionResponse =
        userScenarioCompletionService.markScenarioCompleted(userId, request.scenarioId)

    @GetMapping
    fun getScenarioCompletions(@PathVariable userId: Long): List<UserScenarioCompletionResponse> =
        userScenarioCompletionService.getScenarioCompletionsForUser(userId)
}
