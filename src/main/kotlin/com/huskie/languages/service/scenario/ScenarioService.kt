package com.huskie.languages.service.scenario

import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.VocabularyItem
import com.huskie.languages.dto.scenario.CreateScenarioRequest
import com.huskie.languages.dto.scenario.ScenarioDetailResponse
import com.huskie.languages.dto.scenario.ScenarioLineResponse
import com.huskie.languages.dto.scenario.ScenarioResponse
import com.huskie.languages.dto.scenario.VocabularyItemResponse
import com.huskie.languages.exception.scenario.IncompleteVocabularyCoverageException
import com.huskie.languages.exception.scenario.ScenarioNotFoundException
import com.huskie.languages.repository.scenario.ScenarioLineRepository
import com.huskie.languages.repository.scenario.ScenarioRepository
import com.huskie.languages.repository.scenario.VocabularyItemRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ScenarioService(
    private val scenarioRepository: ScenarioRepository,
    private val scenarioLineRepository: ScenarioLineRepository,
    private val vocabularyItemRepository: VocabularyItemRepository
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
        val vocabularyItemsByLineId = vocabularyItemRepository
            .findAllByScenarioLineIdInOrderByScenarioLineIdAscStartCharIndexAscIdAsc(
                lines.mapNotNull { it.id }
            )
            .groupBy { checkNotNull(it.scenarioLine.id) }

        lines.forEach { line ->
            validateVocabularyCoverage(
                line = line,
                vocabularyItems = vocabularyItemsByLineId[checkNotNull(line.id)].orEmpty()
            )
        }

        return scenario.toDetailResponse(lines, vocabularyItemsByLineId)
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

    private fun Scenario.toDetailResponse(
        lines: List<ScenarioLine>,
        vocabularyItemsByLineId: Map<Long, List<VocabularyItem>>
    ): ScenarioDetailResponse =
        ScenarioDetailResponse(
            id = checkNotNull(id),
            title = title,
            description = description,
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
            hanziText = hanziText,
            pinyinText = pinyinText,
            englishTranslation = englishTranslation,
            createdAt = createdAt,
            vocabularyItems = vocabularyItems.map { it.toResponse() }
        )

    private fun VocabularyItem.toResponse(): VocabularyItemResponse =
        VocabularyItemResponse(
            id = checkNotNull(id),
            expression = expression,
            pinyin = pinyin,
            gloss = gloss,
            explanation = explanation,
            startCharIndex = startCharIndex,
            endCharIndex = endCharIndex,
            createdAt = createdAt
        )

    private fun validateVocabularyCoverage(line: ScenarioLine, vocabularyItems: List<VocabularyItem>) {
        if (vocabularyItems.isEmpty()) {
            throw IncompleteVocabularyCoverageException(line.lineOrder)
        }

        val coveredIndexes = BooleanArray(line.hanziText.length)

        vocabularyItems.forEach { vocabularyItem ->
            val startIndex = vocabularyItem.startCharIndex
            val endIndex = vocabularyItem.endCharIndex

            if (startIndex < 0 || endIndex > line.hanziText.length || startIndex >= endIndex) {
                throw IncompleteVocabularyCoverageException(line.lineOrder)
            }

            val expressionInLine = line.hanziText.substring(startIndex, endIndex)
            if (expressionInLine != vocabularyItem.expression) {
                throw IncompleteVocabularyCoverageException(line.lineOrder)
            }

            for (index in startIndex until endIndex) {
                if (shouldRequireVocabularyCoverage(line.hanziText[index]).not()) {
                    throw IncompleteVocabularyCoverageException(line.lineOrder)
                }
                if (coveredIndexes[index]) {
                    throw IncompleteVocabularyCoverageException(line.lineOrder)
                }
                coveredIndexes[index] = true
            }
        }

        line.hanziText.forEachIndexed { index, character ->
            if (shouldRequireVocabularyCoverage(character) && coveredIndexes[index].not()) {
                throw IncompleteVocabularyCoverageException(line.lineOrder)
            }
        }
    }

    private fun shouldRequireVocabularyCoverage(character: Char): Boolean =
        when (Character.getType(character)) {
            Character.SPACE_SEPARATOR.toInt(),
            Character.LINE_SEPARATOR.toInt(),
            Character.PARAGRAPH_SEPARATOR.toInt(),
            Character.CONNECTOR_PUNCTUATION.toInt(),
            Character.DASH_PUNCTUATION.toInt(),
            Character.START_PUNCTUATION.toInt(),
            Character.END_PUNCTUATION.toInt(),
            Character.INITIAL_QUOTE_PUNCTUATION.toInt(),
            Character.FINAL_QUOTE_PUNCTUATION.toInt(),
            Character.OTHER_PUNCTUATION.toInt() -> false
            else -> true
        }
}
