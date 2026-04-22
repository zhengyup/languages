package com.huskie.languages.dto.scenario

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class UpdateScenarioLineRequest(
    val speakerName: String? = null,
    @field:NotBlank
    val hanziText: String,
    val pinyinText: String? = null,
    val englishTranslation: String? = null,
    @field:Valid
    @field:NotEmpty
    val vocabularyItems: List<UpdateVocabularyItemRequest>
)

data class UpdateVocabularyItemRequest(
    @field:NotBlank
    val expression: String,
    @field:NotBlank
    val pinyin: String,
    @field:NotBlank
    val gloss: String,
    val explanation: String? = null,
    @field:NotNull
    val startCharIndex: Int,
    @field:NotNull
    val endCharIndex: Int
)
