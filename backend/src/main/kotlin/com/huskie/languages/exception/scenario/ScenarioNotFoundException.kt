package com.huskie.languages.exception.scenario

class ScenarioNotFoundException(
    scenarioId: Long
) : RuntimeException("Scenario with id $scenarioId was not found")
