package com.huskie.languages.exception.scenario

class ScenarioLineNotFoundException(
    scenarioId: Long,
    lineId: Long
) : RuntimeException("Scenario line with id $lineId was not found for scenario $scenarioId")
