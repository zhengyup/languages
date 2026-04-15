# DATA_MODEL.md

## 1. Purpose

This document defines the domain model for the Mandarin Scenario Reader backend.

It provides a clear structure for:

- database schema design
- entity relationships
- backend implementation boundaries

The goal is to support a minimal but solid v1 that delivers structured Mandarin learning scenarios with audio and vocabulary explanations.

The model is intentionally simple and avoids premature abstraction.

---

## 2. Domain overview

The application is a structured reading platform centered around realistic Mandarin dialogue scenarios.

Users interact with:

- scenarios
- scenario dialogue lines
- vocabulary explanations linked to the dialogue
- completion tracking for scenarios

The system is content-driven.

Content is manually curated for v1 and stored in the database.

No user-generated content is supported in v1.

---

## 3. Core entities

The v1 system consists of the following entities:

- User
- Scenario
- ScenarioLine
- VocabularyItem
- UserScenarioCompletion

These entities are sufficient to support:

- browsing scenarios
- reading dialogue content
- viewing vocabulary explanations
- listening to audio narration
- tracking completed scenarios

---

## 4. Entity relationships

High-level relationships:

A Scenario has many ScenarioLines.

A Scenario has many VocabularyItems.

A User can complete many Scenarios.

A Scenario can be completed by many Users.

VocabularyItems may optionally reference a specific ScenarioLine.

---

## 5. Entity definitions

### 5.1 User

Represents a learner using the application.

Purpose:
identify the learner and track scenario completion.

Suggested fields:

- id
- email
- displayName
- createdAt
- updatedAt

Notes:

v1 keeps user data minimal.

Authentication complexity should remain simple.

---

### 5.2 Scenario

Represents a structured Mandarin learning scenario.

A scenario is the primary unit of content consumed by the user.

Examples:

ordering food at a restaurant  
asking for directions  
giving a project update

Purpose:

group dialogue text, audio, and vocabulary explanations into a single learning unit.

Suggested fields:

- id
- title
- description
- topic
- difficultyLevel
- audioUrl
- estimatedMinutes
- isPublished
- createdAt
- updatedAt

Notes:

difficultyLevel should use a controlled value.

topic should use a controlled value.

Scenarios should be publishable so unfinished content can remain hidden.

estimatedMinutes helps users choose scenarios based on available time.

---

### 5.3 ScenarioLine

Represents one ordered line of dialogue or passage text.

Lines are displayed sequentially in the reading interface.

Dialogue scenarios may include speaker labels.

Passage scenarios may omit speaker labels.

Purpose:

preserve reading order and allow structured display of dialogue.

Suggested fields:

- id
- scenarioId
- lineOrder
- speakerName
- hanziText
- pinyinText
- englishTranslation
- startTimeMs
- endTimeMs
- createdAt
- updatedAt

Field notes:

lineOrder defines the reading sequence.

speakerName is optional and mainly used for dialogue scenarios.

hanziText is required.

pinyinText is optional.

englishTranslation is optional but recommended.

startTimeMs and endTimeMs allow future synchronization with audio playback.

Constraints:

each ScenarioLine belongs to exactly one Scenario.

lineOrder must be unique within each scenario.

lines should always be retrieved sorted by lineOrder.

---

### 5.4 VocabularyItem

Represents a word or phrase explanation linked to a scenario.

VocabularyItems help users understand difficult words encountered while reading.

Vocabulary is scenario-specific in v1.

Purpose:

provide contextual explanations for words or phrases appearing in the dialogue.

Suggested fields:

- id
- scenarioId
- scenarioLineId
- hanzi
- pinyin
- englishMeaning
- explanation
- createdAt
- updatedAt

Field notes:

hanzi stores the word or phrase in Chinese characters.

pinyin provides pronunciation guidance.

englishMeaning provides a short translation.

explanation optionally provides additional context or nuance.

scenarioLineId is optional but recommended when vocabulary relates to a specific line.

Constraints:

each VocabularyItem belongs to exactly one Scenario.

multiple VocabularyItems may reference the same ScenarioLine.

duplicate entries within the same scenario should be avoided.

VocabularyItem does not need to represent a global dictionary entry in v1.

---

### 5.5 UserScenarioCompletion

Represents that a user has completed a scenario.

Purpose:

track learning progress at the scenario level.

Allows the application to:

show completed scenarios
display progress indicators
encourage continued learning

Suggested fields:

- id
- userId
- scenarioId
- completedAt

Constraints:

each user should only complete a scenario once.

a unique constraint should exist on:

userId + scenarioId

Completion should be explicitly triggered by the user interface.

Example:

"Mark as completed" button.

---

## 6. Suggested enums

Enums should be implemented as controlled values.

---

### 6.1 Scenario topic

Suggested values:

TRAVEL  
WORKPLACE  
DAILY_LIFE  
SOCIAL

These correspond to categories defined in PRODUCT.md.

---

### 6.2 Difficulty level

Suggested values:

INTERMEDIATE  
UPPER_INTERMEDIATE  
ADVANCED

The product targets learners beyond beginner level.

---

## 7. Key invariants

The following rules should always hold:

a Scenario must exist before ScenarioLines can exist.

a Scenario must contain at least one ScenarioLine.

ScenarioLines must have a stable order.

a VocabularyItem must belong to a valid Scenario.

a UserScenarioCompletion must reference a valid User and Scenario.

a user should not complete the same scenario multiple times.

---

## 8. Ownership boundaries

Ownership structure:

Scenario owns:

- ScenarioLines
- VocabularyItems

User owns:

- UserScenarioCompletion

Deleting a Scenario should delete its ScenarioLines and VocabularyItems.

Deleting a User should delete their completion records.

---

## 9. Deletion rules

Recommended behavior:

deleting a Scenario deletes:

- ScenarioLines
- VocabularyItems

deleting a User deletes:

- UserScenarioCompletion records

This can be implemented using foreign key cascade rules or application logic.

---

## 10. V1 simplifications

The following features are intentionally excluded from the data model:

no saved vocabulary lists

no comprehension questions

no spaced repetition system

no grammar explanation entities

no user annotations

no adaptive learning engine

no review scheduling

no collaborative content

These features may be introduced in future versions.

---

## 11. Summary

The v1 data model focuses on delivering structured reading content with minimal complexity.

Content layer:

Scenario  
ScenarioLine  
VocabularyItem

User state:

User  
UserScenarioCompletion

This structure is:

simple to implement  
easy to test  
easy to extend later  
aligned with PRODUCT.md scope