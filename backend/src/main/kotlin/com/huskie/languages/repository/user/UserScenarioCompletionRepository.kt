package com.huskie.languages.repository.user

import com.huskie.languages.domain.user.UserScenarioCompletion
import org.springframework.data.jpa.repository.JpaRepository

interface UserScenarioCompletionRepository : JpaRepository<UserScenarioCompletion, Long> {
    fun existsByUserIdAndScenarioId(userId: Long, scenarioId: Long): Boolean
    fun findAllByUserIdOrderByCompletedAtAsc(userId: Long): List<UserScenarioCompletion>
}
