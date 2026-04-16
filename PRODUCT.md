# PRODUCT.md

## 1. Product Overview

### Product name
Mandarin Scenario Reader

### Summary
A web application that helps intermediate to advanced learners improve Mandarin fluency through realistic dialogue scenarios. Users can read and listen to conversations, tap for vocabulary explanations, and track learning progress.

### Problem
Existing language learning apps often focus on vocabulary memorization, grammar drills, or speaking practice, but provide limited structured exposure to realistic scenario-based dialogue.

Learners at the intermediate level often struggle to:
- understand how Mandarin is used in real-life contexts
- build intuition for natural phrasing
- study effectively in short time windows (e.g. commuting)
- retain vocabulary encountered in meaningful context

There is a need for a structured, scenario-based reading experience that supports both listening and comprehension.

### Target user
Intermediate to advanced Mandarin learners who want to improve fluency for real-world situations such as:
- travel
- workplace communication
- daily life interactions
- social situations

Users are comfortable with basic Mandarin but want more exposure to natural dialogue and practical phrasing.

---

## 2. Goals of v1

The first version of the product should allow users to:

- browse a library of Mandarin learning scenarios
- read realistic dialogue or short passages
- listen to audio narration of the dialogue
- tap or view contextual vocabulary explanations for difficult expressions
- track which scenarios they have completed

The application should provide a smooth reading experience that works well on both desktop and mobile browsers.

---

## 3. Non-goals for v1

The following features are explicitly out of scope for v1:

- simple comprehension questions and answers after reading
- AI conversation or chatbot functionality
- personalized learning recommendations
- adaptive difficulty algorithms
- spaced repetition system
- social or community features
- user-generated content
- gamification features
- offline mode
- mobile native app
- advanced analytics dashboards
- grammar exercise modules

These may be considered for future versions.

---

## 4. Core user flow

The primary user journey for v1:

1. User visits the web application
2. User browses available scenarios by topic or difficulty
3. User selects a scenario
4. User reads the dialogue or passage
5. User listens to audio narration
6. User views contextual phrase glosses for unfamiliar expressions directly inside the reading experience
7. User progress is updated if scenario is completed
8. User returns later to continue learning

---

## 5. Content structure

Each scenario contains structured learning content.

Scenario fields:

- title
- topic
- difficulty level
- short description
- dialogue or passage text
- translation
- optional pinyin
- audio narration
- vocabulary list
- comprehension questions

Dialogue content may be structured line-by-line.

Vocabulary explanations are contextual phrase glosses tied to specific dialogue lines.

Vocabulary items may represent multi-character expressions such as common phrases or verbs.

Vocabulary meaning depends on how the expression is used in that specific line.

The goal is to help learners understand difficult expressions without leaving the app for external lookup.

---

## 6. Scenario categories

Initial scenario categories for v1:

- Travel
    - ordering food
    - asking for directions
    - checking into hotel
    - transportation situations

- Workplace
    - introducing yourself
    - discussing project updates
    - scheduling meetings
    - asking for clarification

- Daily life
    - shopping
    - making appointments
    - casual conversations
    - hobbies

- Social interactions
    - meeting new people
    - making recommendations
    - polite disagreement
    - invitations

---

## 7. Success criteria

v1 is considered successful if:

- users can complete at least one scenario end-to-end
- users can read and listen to dialogue smoothly
- user progress is persisted correctly
- scenarios load quickly
- reading interface works well on mobile browser
- vocabulary explanations are accessible during reading and reduce the need for external lookup

---

## 8. Constraints

Technical constraints:

- backend implemented using Kotlin and Spring Boot
- database is PostgreSQL
- application is web-first
- content library initially contains 10–20 scenarios
- content is manually curated for v1

Scope constraints:

- avoid over-engineering infrastructure
- prioritize readability and usability
- keep initial feature set minimal

---

## 9. Future ideas (not part of v1)

Potential future improvements:

- AI-generated scenario content
- AI conversation practice based on scenarios
- personalized difficulty progression
- spaced repetition vocabulary review
- grammar explanation layer
- pronunciation feedback
- offline reading mode
- mobile native app
- advanced analytics on learning patterns
- collaborative annotations
