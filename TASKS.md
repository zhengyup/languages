# TASK 1 — Create Scenario entity

Goal:
Verify the backend can persist and retrieve data from PostgreSQL.

Scope:

Create Scenario entity with fields:
- id
- title
- description
- topic
- difficultyLevel
- createdAt

Implement:

ScenarioRepository using Spring Data JPA

ScenarioService with:
- createScenario
- getAllScenarios

ScenarioController with endpoints:

POST /scenarios
GET /scenarios

Add integration test that:

creates a scenario
retrieves scenarios
verifies stored values match

Constraints:

Use Kotlin
Use constructor injection
Follow Clean Code naming conventions
Keep service thin
Use DTOs for request and response
Do not include business logic yet