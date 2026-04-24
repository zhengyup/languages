package com.huskie.languages.service.scenario

import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.VocabularyItem
import com.huskie.languages.dto.scenario.CreateScenarioRequest
import com.huskie.languages.dto.scenario.ScenarioDetailResponse
import com.huskie.languages.dto.scenario.ScenarioLineResponse
import com.huskie.languages.dto.scenario.ScenarioResponse
import com.huskie.languages.dto.scenario.UpdateScenarioLineRequest
import com.huskie.languages.dto.scenario.VocabularyItemResponse
import com.huskie.languages.exception.scenario.ScenarioLineNotFoundException
import com.huskie.languages.exception.scenario.ScenarioNotFoundException
import com.huskie.languages.repository.scenario.ScenarioLineRepository
import com.huskie.languages.repository.scenario.ScenarioRepository
import com.huskie.languages.repository.scenario.VocabularyItemRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ScenarioService(
    private val scenarioRepository: ScenarioRepository,
    private val scenarioLineRepository: ScenarioLineRepository,
    private val vocabularyItemRepository: VocabularyItemRepository,
    private val scenarioLineVocabularyCoverageValidator: ScenarioLineVocabularyCoverageValidator
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
        val vocabularyItemsByLineId = getVocabularyItemsByLineId(lines)

        validateVocabularyCoverage(lines, vocabularyItemsByLineId)

        return scenario.toDetailResponse(lines, vocabularyItemsByLineId)
    }

    @Transactional
    fun updateScenarioLine(
        scenarioId: Long,
        lineId: Long,
        request: UpdateScenarioLineRequest
    ): ScenarioLineResponse {
        val scenarioLine = scenarioLineRepository.findByIdAndScenarioId(lineId, scenarioId)
            ?: throw ScenarioLineNotFoundException(scenarioId, lineId)

        val updatedLine = buildUpdatedScenarioLine(scenarioLine, request)
        val replacementVocabularyItems = buildReplacementVocabularyItems(updatedLine, request)

        scenarioLineVocabularyCoverageValidator.validate(updatedLine, replacementVocabularyItems)

        val savedLine = scenarioLineRepository.save(updatedLine)
        replaceVocabularyItemsForLine(savedLine, replacementVocabularyItems)

        return savedLine.toResponse(replacementVocabularyItems)
    }

    private fun getVocabularyItemsByLineId(lines: List<ScenarioLine>): Map<Long, List<VocabularyItem>> {
        val lineIds = lines.mapNotNull { it.id }

        return vocabularyItemRepository
            .findAllByScenarioLineIdInOrderByScenarioLineIdAscStartCharIndexAscIdAsc(lineIds)
            .groupBy { checkNotNull(it.scenarioLine.id) }
    }

    private fun validateVocabularyCoverage(
        lines: List<ScenarioLine>,
        vocabularyItemsByLineId: Map<Long, List<VocabularyItem>>
    ) {
        lines.forEach { line ->
            scenarioLineVocabularyCoverageValidator.validate(
                line = line,
                vocabularyItems = vocabularyItemsByLineId[checkNotNull(line.id)].orEmpty()
            )
        }
    }

    private fun buildUpdatedScenarioLine(
        scenarioLine: ScenarioLine,
        request: UpdateScenarioLineRequest
    ): ScenarioLine =
        scenarioLine.withUpdatedContent(
            speakerName = request.speakerName?.trim(),
            targetText = request.targetText.trim(),
            pronunciationGuide = request.pronunciationGuide?.trim(),
            englishTranslation = request.englishTranslation?.trim()
        )

    private fun buildReplacementVocabularyItems(
        scenarioLine: ScenarioLine,
        request: UpdateScenarioLineRequest
    ): List<VocabularyItem> =
        request.vocabularyItems.map {
            VocabularyItem(
                scenarioLine = scenarioLine,
                expression = it.expression.trim(),
                pronunciationGuide = it.pronunciationGuide.trim(),
                gloss = it.gloss.trim(),
                explanation = it.explanation?.trim(),
                startCharIndex = it.startCharIndex,
                endCharIndex = it.endCharIndex,
                createdAt = Instant.now()
            )
        }

    private fun replaceVocabularyItemsForLine(
        scenarioLine: ScenarioLine,
        vocabularyItems: List<VocabularyItem>
    ) {
        vocabularyItemRepository.deleteAllByScenarioLineId(checkNotNull(scenarioLine.id))
        vocabularyItemRepository.saveAll(
            vocabularyItems.map {
                VocabularyItem(
                    scenarioLine = scenarioLine,
                    expression = it.expression,
                    pronunciationGuide = it.pronunciationGuide,
                    gloss = it.gloss,
                    explanation = it.explanation,
                    startCharIndex = it.startCharIndex,
                    endCharIndex = it.endCharIndex,
                    createdAt = it.createdAt
                )
            }
        )
    }

    private fun Scenario.toResponse(): ScenarioResponse =
        ScenarioResponse(
            id = checkNotNull(id),
            title = title,
            description = description,
            language = language,
            topic = topic,
            difficultyLevel = difficultyLevel,
            createdAt = createdAt
        )

    private fun Scenario.toDetailResponse(
        lines: List<ScenarioLine>,
        vocabularyItemsByLineId: Map<Long, List<VocabularyItem>>
    ): ScenarioDetailResponse =
        ScenarioDetailResponse(
            id = checkNotNull(id),
            title = title,
            description = description,
            language = language,
            topic = topic,
            difficultyLevel = difficultyLevel,
            createdAt = createdAt,
            lines = lines.map {
                it.toResponse(vocabularyItemsByLineId[checkNotNull(it.id)].orEmpty())
            }
        )

    private fun ScenarioLine.toResponse(vocabularyItems: List<VocabularyItem>): ScenarioLineResponse =
        ScenarioLineResponse(
            id = checkNotNull(id),
            lineOrder = lineOrder,
            speakerName = speakerName,
            targetText = targetText,
            pronunciationGuide = pronunciationGuide,
            englishTranslation = englishTranslation,
            audioUrl = audioUrl,
            createdAt = createdAt,
            vocabularyItems = vocabularyItems.map { it.toResponse() }
        )

    private fun VocabularyItem.toResponse(): VocabularyItemResponse =
        VocabularyItemResponse(
            id = checkNotNull(id),
            expression = expression,
            pronunciationGuide = pronunciationGuide,
            gloss = gloss,
            explanation = explanation,
            startCharIndex = startCharIndex,
            endCharIndex = endCharIndex,
            createdAt = createdAt
        )
}
