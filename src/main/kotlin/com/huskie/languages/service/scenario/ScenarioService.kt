package com.huskie.languages.service.scenario

import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.dto.scenario.CreateScenarioRequest
import com.huskie.languages.dto.scenario.ScenarioDetailResponse
import com.huskie.languages.dto.scenario.ScenarioLineResponse
import com.huskie.languages.dto.scenario.ScenarioResponse
import com.huskie.languages.exception.scenario.ScenarioNotFoundException
import com.huskie.languages.repository.scenario.ScenarioLineRepository
import com.huskie.languages.repository.scenario.ScenarioRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ScenarioService(
    private val scenarioRepository: ScenarioRepository,
    private val scenarioLineRepository: ScenarioLineRepository
) {
    fun createScenario(request: CreateScenarioRequest): ScenarioResponse {
        val scenario = Scenario(
            title = request.title.trim(),
            description = request.description.trim(),
            topic = request.topic,
            difficultyLevel = request.difficultyLevel,
            createdAt = Instant.now()
        )

        return scenarioRepository.save(scenario).toResponse()
    }

    fun getAllScenarios(): List<ScenarioResponse> =
        scenarioRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt")).map { it.toResponse() }

    fun getScenarioById(id: Long): ScenarioDetailResponse {
        val scenario = scenarioRepository.findById(id)
            .orElseThrow { ScenarioNotFoundException(id) }
        val lines = scenarioLineRepository.findAllByScenarioIdOrderByLineOrderAsc(id)

        return scenario.toDetailResponse(lines)
    }

    private fun Scenario.toResponse(): ScenarioResponse =
        ScenarioResponse(
            id = checkNotNull(id),
            title = title,
            description = description,
            topic = topic,
            difficultyLevel = difficultyLevel,
            createdAt = createdAt
        )

    private fun Scenario.toDetailResponse(lines: List<ScenarioLine>): ScenarioDetailResponse =
        ScenarioDetailResponse(
            id = checkNotNull(id),
            title = title,
            description = description,
            topic = topic,
            difficultyLevel = difficultyLevel,
            createdAt = createdAt,
            lines = lines.map { it.toResponse() }
        )

    private fun ScenarioLine.toResponse(): ScenarioLineResponse =
        ScenarioLineResponse(
            id = checkNotNull(id),
            lineOrder = lineOrder,
            speakerName = speakerName,
            hanziText = hanziText,
            pinyinText = pinyinText,
            englishTranslation = englishTranslation,
            createdAt = createdAt
        )
}
