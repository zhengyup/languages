package com.huskie.languages.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.LearningLanguage
import com.huskie.languages.domain.scenario.ScenarioTopic
import org.springframework.stereotype.Component

@Component
class JapaneseScenarioSeedDataProvider : ScenarioSeedDataProvider {
    override val language: LearningLanguage = LearningLanguage.JAPANESE
    private val objectMapper = ObjectMapper()

    override fun scenarios(): List<SeedScenario> {
        val inputStream = javaClass.getResourceAsStream("/seed-data/japanese_seed_scenarios_full.json")
            ?: error("Missing Japanese seed data resource.")

        inputStream.use {
            val payload = objectMapper.readTree(it)
            return payload.map(::toSeedScenario)
        }
    }

    private fun toSeedScenario(payload: JsonNode): SeedScenario =
        SeedScenario(
            title = payload.requiredText("title"),
            description = payload.requiredText("description"),
            language = language,
            topic = payload.toScenarioTopic(),
            difficultyLevel = DifficultyLevel.valueOf(payload.requiredText("difficultyLevel")),
            lines = payload.requiredArray("lines")
                .sortedBy { it.requiredInt("lineOrder") }
                .map(::toSeedLine)
        ).also {
            require(payload.requiredText("language") == language.name) {
                "Unexpected language '${payload.requiredText("language")}' in Japanese seed data."
            }
        }

    private fun toSeedLine(payload: JsonNode): SeedLine =
        SeedLine(
            speakerName = payload.requiredText("speakerName"),
            targetText = payload.requiredText("targetText"),
            pronunciationGuide = payload.requiredText("pronunciationGuide"),
            englishTranslation = payload.requiredText("englishTranslation"),
            vocabularyItems = payload.requiredArray("vocabularyItems").map(::toSeedVocabularyItem)
        )

    private fun toSeedVocabularyItem(payload: JsonNode): SeedVocabularyItem =
        SeedVocabularyItem(
            expression = payload.requiredText("expression"),
            pronunciationGuide = payload.requiredText("pronunciationGuide"),
            gloss = payload.requiredText("gloss"),
            explanation = payload.optionalText("explanation"),
            startCharIndex = payload.requiredInt("startCharIndex"),
            endCharIndex = payload.requiredInt("endCharIndex")
        )

    private fun JsonNode.toScenarioTopic(): ScenarioTopic =
        when (requiredText("topic").trim().lowercase()) {
            "ordering food", "cafe" -> ScenarioTopic.RESTAURANT
            "asking for directions", "train station", "travel", "hotel" -> ScenarioTopic.TRAVEL
            "shopping", "convenience store" -> ScenarioTopic.SHOPPING
            "small talk", "casual small talk", "introductions", "health" -> ScenarioTopic.WORK
            else -> error("Unsupported Japanese seed topic: ${requiredText("topic")}")
        }

    private fun JsonNode.requiredArray(fieldName: String): List<JsonNode> =
        this[fieldName]?.toList() ?: error("Missing array field '$fieldName' in Japanese seed data.")

    private fun JsonNode.requiredText(fieldName: String): String =
        this[fieldName]?.asText() ?: error("Missing text field '$fieldName' in Japanese seed data.")

    private fun JsonNode.optionalText(fieldName: String): String? =
        this[fieldName]?.takeIf { it.isNull.not() }?.asText()

    private fun JsonNode.requiredInt(fieldName: String): Int =
        this[fieldName]?.asInt() ?: error("Missing integer field '$fieldName' in Japanese seed data.")
}
