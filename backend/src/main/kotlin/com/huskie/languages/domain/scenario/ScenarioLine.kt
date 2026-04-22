package com.huskie.languages.domain.scenario

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "scenario_lines")
class ScenarioLine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scenario_id", nullable = false)
    val scenario: Scenario,
    @Column(name = "line_order", nullable = false)
    val lineOrder: Int,
    @Column(name = "speaker_name")
    val speakerName: String? = null,
    @Column(name = "hanzi_text", nullable = false)
    val hanziText: String,
    @Column(name = "pinyin_text")
    val pinyinText: String? = null,
    @Column(name = "english_translation")
    val englishTranslation: String? = null,
    @Column(name = "audio_url")
    val audioUrl: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "audio_status", nullable = false)
    val audioStatus: AudioStatus = AudioStatus.NOT_GENERATED,
    @Column(name = "audio_generated_at")
    val audioGeneratedAt: Instant? = null,
    @Column(name = "audio_source_text_hash")
    val audioSourceTextHash: String? = null,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,
    @OneToMany(mappedBy = "scenarioLine")
    val vocabularyItems: List<VocabularyItem> = emptyList()
)
