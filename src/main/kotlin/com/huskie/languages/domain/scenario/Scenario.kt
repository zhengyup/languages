package com.huskie.languages.domain.scenario

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "scenarios")
class Scenario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val description: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val topic: ScenarioTopic,
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    val difficultyLevel: DifficultyLevel,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,
    @OneToMany(mappedBy = "scenario")
    val lines: List<ScenarioLine> = emptyList()
)
