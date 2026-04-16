package com.huskie.languages.domain.user

import com.huskie.languages.domain.scenario.Scenario
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "user_scenario_completions",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_user_scenario_completions_user_scenario",
            columnNames = ["user_id", "scenario_id"]
        )
    ]
)
class UserScenarioCompletion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scenario_id", nullable = false)
    val scenario: Scenario,
    @Column(name = "completed_at", nullable = false, updatable = false)
    val completedAt: Instant
)
