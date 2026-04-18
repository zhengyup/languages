package com.huskie.languages.controller.scenario

import com.huskie.languages.dto.scenario.CreateScenarioRequest
import com.huskie.languages.dto.scenario.ScenarioDetailResponse
import com.huskie.languages.dto.scenario.ScenarioResponse
import com.huskie.languages.service.scenario.ScenarioService
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
@RequestMapping("/scenarios")
class ScenarioController(
    private val scenarioService: ScenarioService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createScenario(@Valid @RequestBody request: CreateScenarioRequest): ScenarioResponse =
        scenarioService.createScenario(request)

    @GetMapping
    fun getAllScenarios(): List<ScenarioResponse> =
        scenarioService.getAllScenarios()

    @GetMapping("/{id}")
    fun getScenarioById(@PathVariable id: Long): ScenarioDetailResponse =
        scenarioService.getScenarioById(id)
}
