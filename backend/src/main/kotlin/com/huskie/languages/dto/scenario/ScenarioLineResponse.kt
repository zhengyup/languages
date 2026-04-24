package com.huskie.languages.dto.scenario

import java.time.Instant

data class ScenarioLineResponse(
    val id: Long,
    val lineOrder: Int,
    val speakerName: String?,
    val targetText: String,
    val pronunciationGuide: String?,
    val englishTranslation: String?,
    val audioUrl: String?,
    val createdAt: Instant,
    val vocabularyItems: List<VocabularyItemResponse>
)
