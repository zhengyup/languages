package com.huskie.languages.domain.scenario

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "vocabulary_items")
class VocabularyItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scenario_line_id", nullable = false)
    val scenarioLine: ScenarioLine,
    @Column(nullable = false)
    val expression: String,
    @Column(nullable = false)
    val pinyin: String,
    @Column(name = "pronunciation_guide")
    val pronunciationGuide: String? = pinyin,
    @Column(nullable = false)
    val gloss: String,
    @Column
    val explanation: String? = null,
    @Column(name = "start_char_index")
    val startCharIndex: Int,
    @Column(name = "end_char_index")
    val endCharIndex: Int,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant
)
