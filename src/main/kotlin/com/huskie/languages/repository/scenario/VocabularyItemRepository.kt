package com.huskie.languages.repository.scenario

import com.huskie.languages.domain.scenario.VocabularyItem
import org.springframework.data.jpa.repository.JpaRepository

interface VocabularyItemRepository : JpaRepository<VocabularyItem, Long> {
    fun findAllByScenarioLineIdInOrderByScenarioLineIdAscIdAsc(scenarioLineIds: Collection<Long>): List<VocabularyItem>
}
