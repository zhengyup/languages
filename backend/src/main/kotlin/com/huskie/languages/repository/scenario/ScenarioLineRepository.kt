package com.huskie.languages.repository.scenario

import com.huskie.languages.domain.scenario.ScenarioLine
import org.springframework.data.jpa.repository.JpaRepository

interface ScenarioLineRepository : JpaRepository<ScenarioLine, Long> {
    fun findAllByScenarioIdOrderByLineOrderAsc(scenarioId: Long): List<ScenarioLine>
}
