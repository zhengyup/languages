package com.huskie.languages.config

import com.huskie.languages.domain.scenario.LearningLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JapaneseScenarioSeedDataProviderTest {
    private val provider = JapaneseScenarioSeedDataProvider()

    @Test
    fun shouldLoadJapaneseSeedScenariosFromResource() {
        val scenarios = provider.scenarios()

        assertEquals(12, scenarios.size)
        assertTrue(scenarios.all { it.language == LearningLanguage.JAPANESE })
        assertTrue(scenarios.all { it.lines.isNotEmpty() })
        assertTrue(scenarios.flatMap { it.lines }.all { it.vocabularyItems.isNotEmpty() })
    }
}
