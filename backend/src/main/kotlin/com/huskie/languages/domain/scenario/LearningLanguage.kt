package com.huskie.languages.domain.scenario

enum class LearningLanguage {
    MANDARIN,
    KOREAN,
    JAPANESE,
    SPANISH,
    GERMAN;

    fun isReaderVisible(): Boolean =
        this in READER_VISIBLE_LANGUAGES

    companion object {
        val READER_VISIBLE_LANGUAGES: Set<LearningLanguage> = setOf(
            MANDARIN,
            KOREAN,
            JAPANESE
        )
    }
}
