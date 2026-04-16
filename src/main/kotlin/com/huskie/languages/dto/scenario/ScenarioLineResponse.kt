package com.huskie.languages.dto.scenario

import java.time.Instant

data class ScenarioLineResponse(
    val id: Long,
    val lineOrder: Int,
    val speakerName: String?,
    val hanziText: String,
    val pinyinText: String?,
    val englishTranslation: String?,
    val createdAt: Instant
)
