# DATA_MODEL.md

## 1. Purpose

This document defines the core domain model for the Mandarin Scenario Reader backend.

It is intended to guide:

- database schema design
- entity modelling
- API design
- service responsibilities

This file describes the domain at a conceptual level.

It should remain stable even if implementation details evolve.

---

## 2. Domain overview

The application is centered around structured Mandarin learning content.

A user interacts with:

- scenarios
- scenario lines or passages
- vocabulary items
- comprehension questions
- saved vocabulary
- learning progress
- scenario attempts

The system is designed as a content-driven learning platform.

For v1, the system is intentionally simple:
- scenarios are curated content
- vocabulary is manually attached to scenarios
- progress is tracked per user per scenario
- comprehension is measured through simple questions

---

## 3. Core entities

The main entities in v1 are:

- User
- Scenario
- ScenarioLine
- VocabularyItem
- ScenarioQuestion
- ScenarioQuestionOption
- UserScenarioProgress
- SavedWord
- ScenarioAttempt

---

## 4. Entity relationships

High-level relationships:

- A Scenario has many ScenarioLines
- A Scenario has many VocabularyItems
- A Scenario has many ScenarioQuestions
- A ScenarioQuestion has many ScenarioQuestionOptions
- A User has many UserScenarioProgress records
- A User has many SavedWords
- A User has many ScenarioAttempts

A UserScenarioProgress belongs to:
- one User
- one Scenario

A SavedWord belongs to:
- one User
- one VocabularyItem
- optionally one Scenario

A ScenarioAttempt belongs to:
- one User
- one Scenario

---

## 5. Entity definitions

### 5.1 User

Represents an authenticated learner using the application.

Purpose:
- identify the learner
- store learning-related state
- link progress and saved vocabulary

Suggested fields:
- id
- email
- displayName
- createdAt
- updatedAt

Notes:
- v1 only needs minimal user profile information
- authentication complexity should remain low

---

### 5.2 Scenario

Represents a single learning scenario.

A scenario is the main unit of learning content.

Examples:
- ordering food at a restaurant
- asking for directions
- giving a project update in a meeting

Purpose:
- group together text, vocabulary, audio, and questions
- serve as the main content item a user consumes

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
- difficultyLevel should be represented using a controlled enum or string
- topic should be represented using a controlled enum or string
- scenarios should be publishable and hideable

---

### 5.3 ScenarioLine

Represents a single ordered line or segment within a scenario.

For dialogue-based scenarios, this can represent one spoken line.
For passage-based scenarios, this can represent one sentence or paragraph chunk.

Purpose:
- preserve the reading order of a scenario
- support line-by-line reading UI
- optionally support audio synchronization later

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

Notes:
- speakerName is optional and mainly useful for dialogues
- startTimeMs and endTimeMs are optional in v1
- lineOrder must be unique within a scenario

Constraints:
- every ScenarioLine must belong to exactly one Scenario
- lines must have a stable order within the scenario

---

### 5.4 VocabularyItem

Represents a word or phrase that the learner may want to understand while reading a scenario.

Purpose:
- provide structured vocabulary explanations
- support saved words
- support contextual review later

Suggested fields:
- id
- scenarioId
- hanzi
- pinyin
- englishMeaning
- explanation
- lineReference
- startCharIndex
- endCharIndex
- createdAt
- updatedAt

Notes:
- v1 can keep vocabulary attached directly to a scenario
- lineReference may point to a ScenarioLine if the vocabulary item is tied to a specific line
- startCharIndex and endCharIndex are optional and may be added only if inline text highlighting is needed
- vocabulary can represent either a single word or multi-character phrase

Constraints:
- every VocabularyItem must belong to exactly one Scenario in v1
- duplicate vocabulary within the same scenario should be avoided where possible

Future note:
- later versions may support a global vocabulary dictionary with scenario-specific usage examples

---

### 5.5 ScenarioQuestion

Represents a comprehension question attached to a scenario.

Purpose:
- test whether the user understood the scenario
- provide lightweight learning feedback

Suggested fields:
- id
- scenarioId
- questionOrder
- questionText
- questionType
- explanation
- createdAt
- updatedAt

Notes:
- v1 should support multiple choice questions only
- questionType can still exist for extensibility
- explanation is optional and can be shown after submission

Constraints:
- every ScenarioQuestion must belong to exactly one Scenario
- questionOrder must be unique within a scenario

---

### 5.6 ScenarioQuestionOption

Represents one answer option for a comprehension question.

Purpose:
- define possible answers for a ScenarioQuestion
- indicate which option is correct

Suggested fields:
- id
- questionId
- optionOrder
- optionText
- isCorrect
- createdAt
- updatedAt

Constraints:
- every ScenarioQuestionOption must belong to exactly one ScenarioQuestion
- each question must have at least one correct option
- optionOrder must be unique within a question

---

### 5.7 UserScenarioProgress

Represents a learner's current progress for a specific scenario.

Purpose:
- track whether a scenario was started or completed
- store last reading position
- support resume functionality

Suggested fields:
- id
- userId
- scenarioId
- status
- lastReadLineOrder
- startedAt
- completedAt
- updatedAt

Suggested status values:
- NOT_STARTED
- IN_PROGRESS
- COMPLETED

Notes:
- v1 should track progress at the scenario level
- line-level resume support can be approximated with lastReadLineOrder

Constraints:
- one user should have at most one progress record per scenario

---

### 5.8 SavedWord

Represents a vocabulary item saved by a learner.

Purpose:
- allow users to build a personal list of useful words and phrases
- support future review flows

Suggested fields:
- id
- userId
- vocabularyItemId
- scenarioId
- savedAt

Notes:
- storing scenarioId redundantly is acceptable if it simplifies retrieval
- v1 should avoid duplicate saves of the same vocabulary item by the same user

Constraints:
- one user should not save the same vocabulary item more than once

---

### 5.9 ScenarioAttempt

Represents one attempt by a user to answer a scenario's comprehension questions.

Purpose:
- record question completion activity
- store assessment results
- enable basic progress analytics later

Suggested fields:
- id
- userId
- scenarioId
- score
- totalQuestions
- correctAnswers
- attemptedAt

Notes:
- v1 can store only the aggregate result
- question-by-question answer history can be deferred

Constraints:
- every ScenarioAttempt must belong to one User and one Scenario

Future note:
- later versions may introduce a ScenarioAttemptAnswer entity for per-question answer tracking

---

## 6. Suggested enums / controlled values

These should be represented as enums or constrained string values in the implementation.

### 6.1 Scenario topic

Possible values for v1:
- TRAVEL
- WORKPLACE
- DAILY_LIFE
- SOCIAL

### 6.2 Difficulty level

Possible values for v1:
- BEGINNER
- INTERMEDIATE
- ADVANCED

If your actual target users are stronger learners, you may instead choose:
- LOWER_INTERMEDIATE
- INTERMEDIATE
- UPPER_INTERMEDIATE
- ADVANCED

### 6.3 Progress status

Possible values:
- NOT_STARTED
- IN_PROGRESS
- COMPLETED

### 6.4 Question type

Possible values for v1:
- MULTIPLE_CHOICE

Future values:
- TRUE_FALSE
- SHORT_ANSWER

---

## 7. Key invariants

The following rules should remain true across the system:

- A Scenario must exist before any ScenarioLine, VocabularyItem, or ScenarioQuestion can exist for it
- A Scenario must have at least one ScenarioLine
- Scenario lines must have a stable ordering
- A ScenarioQuestion must have at least one correct option
- A User can have only one progress record per Scenario
- A User should not save the same VocabularyItem multiple times
- A ScenarioAttempt must be linked to a valid User and Scenario

---

## 8. Ownership boundaries

Ownership rules for v1:

Scenario owns:
- ScenarioLines
- VocabularyItems
- ScenarioQuestions

ScenarioQuestion owns:
- ScenarioQuestionOptions

User owns:
- UserScenarioProgress
- SavedWords
- ScenarioAttempts

This helps keep deletion and lifecycle rules simple.

---

## 9. Deletion rules

Suggested deletion behavior:

- deleting a Scenario should delete its ScenarioLines
- deleting a Scenario should delete its VocabularyItems
- deleting a Scenario should delete its ScenarioQuestions and their options
- deleting a User should delete their progress, saved words, and attempts

In implementation, this can be handled with either:
- database foreign key cascade rules
- application-managed deletion logic

Prefer simplicity and consistency.

---

## 10. V1 simplifications

To keep v1 manageable, the following simplifications are intentional:

- vocabulary is scenario-scoped rather than globally deduplicated
- comprehension attempts store only aggregate score
- no user-generated annotations
- no global dictionary or morphological analysis
- no adaptive progression model
- no spaced repetition data model
- no pronunciation tracking
- no social learning features

These choices are intentional and should not be expanded unless explicitly requested.

---

## 11. Future extensibility

Possible later additions include:

- GlobalVocabularyEntry
- ScenarioTag
- GrammarNote
- UserNote
- ScenarioAttemptAnswer
- RecommendedScenario
- ReviewQueueItem
- AudioSegmentMetadata

These are not part of v1 and should not be implemented yet.

---

## 12. Summary

The v1 data model is centered around three areas:

Content:
- Scenario
- ScenarioLine
- VocabularyItem
- ScenarioQuestion
- ScenarioQuestionOption

User state:
- User
- UserScenarioProgress
- SavedWord

Assessment:
- ScenarioAttempt

This structure is intended to be:
- simple
- extensible
- easy to map to relational tables
- suitable for a web-first MVP