package com.huskie.languages.dto.scenario

import java.time.Instant

data class VocabularyItemResponse(
    val id: Long,
    val expression: String,
    val pinyin: String,
    val gloss: String,
    val explanation: String?,
    val startCharIndex: Int,
    val endCharIndex: Int,
    val createdAt: Instant
)
