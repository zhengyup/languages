package com.huskie.languages.exception.user

class DuplicateUserScenarioCompletionException(
    userId: Long,
    scenarioId: Long
) : RuntimeException("User $userId has already completed scenario $scenarioId")
