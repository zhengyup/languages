package com.huskie.languages.service.scenario

import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.VocabularyItem
import com.huskie.languages.exception.scenario.IncompleteVocabularyCoverageException
import org.springframework.stereotype.Component

@Component
class ScenarioLineVocabularyCoverageValidator {
    fun validate(line: ScenarioLine, vocabularyItems: List<VocabularyItem>) {
        if (vocabularyItems.isEmpty()) {
            throw IncompleteVocabularyCoverageException(line.lineOrder)
        }

        val coveredIndexes = BooleanArray(line.targetText.length)

        vocabularyItems.forEach { vocabularyItem ->
            validateVocabularyItem(line, vocabularyItem, coveredIndexes)
        }

        line.targetText.forEachIndexed { index, character ->
            if (requiresCoverage(character) && coveredIndexes[index].not()) {
                throw IncompleteVocabularyCoverageException(line.lineOrder)
            }
        }
    }

    private fun validateVocabularyItem(
        line: ScenarioLine,
        vocabularyItem: VocabularyItem,
        coveredIndexes: BooleanArray
    ) {
        validateSpanRange(line, vocabularyItem)
        validateExpressionMatch(line, vocabularyItem)
        markCoveredIndexes(line, vocabularyItem, coveredIndexes)
    }

    private fun validateSpanRange(line: ScenarioLine, vocabularyItem: VocabularyItem) {
        val startIndex = vocabularyItem.startCharIndex
        val endIndex = vocabularyItem.endCharIndex

        if (startIndex < 0 || endIndex > line.targetText.length || startIndex >= endIndex) {
            throw IncompleteVocabularyCoverageException(line.lineOrder)
        }
    }

    private fun validateExpressionMatch(line: ScenarioLine, vocabularyItem: VocabularyItem) {
        val expressionInLine = line.targetText.substring(
            vocabularyItem.startCharIndex,
            vocabularyItem.endCharIndex
        )

        if (expressionInLine != vocabularyItem.expression) {
            throw IncompleteVocabularyCoverageException(line.lineOrder)
        }
    }

    private fun markCoveredIndexes(
        line: ScenarioLine,
        vocabularyItem: VocabularyItem,
        coveredIndexes: BooleanArray
    ) {
        for (index in vocabularyItem.startCharIndex until vocabularyItem.endCharIndex) {
            if (requiresCoverage(line.targetText[index]).not() || coveredIndexes[index]) {
                throw IncompleteVocabularyCoverageException(line.lineOrder)
            }
            coveredIndexes[index] = true
        }
    }

    private fun requiresCoverage(character: Char): Boolean =
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
