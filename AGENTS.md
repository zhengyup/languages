# AGENTS.md

## Purpose

This file provides instructions for AI coding agents (e.g. Codex) working in this repository.

Agents must follow the conventions and constraints defined here when generating or modifying code.

The goal of this project is to build a structured Mandarin learning backend with clean architecture, minimal complexity, and production-style code quality.

Agents should prioritize clarity, correctness, and maintainability over cleverness or over-engineering.

---

## Project Overview

This repository contains the backend for a Mandarin scenario-based learning application.

Users will be able to:

- browse structured learning scenarios
- read dialogue passages
- view vocabulary explanations
- save vocabulary items
- track reading progress
- answer comprehension questions

The project is intentionally scoped as a **web-first MVP** with a clean backend architecture.

Refer to:

PRODUCT.md for product scope  
DATA_MODEL.md for domain structure  
API_SPEC.md for endpoints  
TASKS.md for implementation steps

---

## Tech Stack

Language:
Kotlin

Framework:
Spring Boot

Database:
PostgreSQL

ORM:
Spring Data JPA (Hibernate)

Migrations:
Flyway

Build tool:
Gradle Kotlin DSL

Serialization:
Jackson

Validation:
Jakarta Validation

Testing:
JUnit 5

---

## Architecture Principles

Follow a layered architecture.

Controller layer:
Handles HTTP requests and responses.

Service layer:
Contains business logic.

Repository layer:
Handles database interaction.

Entity layer:
Represents persistent domain models.

DTO layer:
Used for request and response objects.

Avoid placing business logic inside controllers or repositories.

---

## Package Structure

Follow this structure:

com.example.mandarin

config
controller
service
repository
domain
dto
mapper
exception

Each domain concept should remain grouped logically.

Example:

domain/scenario
repository/scenario
service/scenario
controller/scenario

---

## Coding Guidelines

Prefer simple and readable code.

Avoid unnecessary abstraction.

Avoid premature optimization.

Prefer explicit over implicit behaviour.

Use descriptive naming.

Avoid single-letter variable names.

Avoid deeply nested logic.

Keep functions small and focused.

Prefer composition over inheritance.

---

## API Design Guidelines

Follow REST conventions.

Use plural resource names.

Examples:

GET /api/scenarios
GET /api/scenarios/{id}
POST /api/saved-words
POST /api/progress

Return DTOs, not entities.

Avoid exposing internal database structure directly.

Use consistent response formats.

---

## Database Guidelines

Use PostgreSQL as the primary data store.

Use Flyway for schema migrations.

Every schema change must include a migration.

Migration files should be placed in:

resources/db/migration

Naming convention:

V1__create_scenario_tables.sql
V2__create_vocab_tables.sql

Avoid modifying existing migration files after they are applied.

Create new migrations instead.

---

## Entity Guidelines

Entities should:

represent domain concepts
avoid business logic
contain only persistence-related annotations

Use Kotlin data classes where appropriate.

Use explicit relationships:

OneToMany
ManyToOne
JoinColumn

Avoid bidirectional relationships unless necessary.

Prefer unidirectional relationships for simplicity.

---

## DTO Guidelines

Use DTOs for:

request validation
response formatting

Do not expose entities directly through APIs.

DTOs should be placed in:

dto package

Prefer separate DTOs for:

request objects
response objects

---

## Testing Guidelines

Write tests for:

service logic
repository queries
API endpoints

Prefer integration tests for API endpoints.

Use descriptive test names.

Example:

shouldReturnScenarioList
shouldSaveVocabularyItem
shouldPersistUserProgress

Tests should be deterministic.

Avoid external dependencies in tests.

---

## Scope Control

Do not introduce additional frameworks or infrastructure unless explicitly requested.

Do not add:

Kafka
Redis
GraphQL
Elasticsearch
OAuth2
Microservices architecture
Event sourcing
Reactive stack

Keep the application as a modular monolith.

---

## Feature Constraints

Follow PRODUCT.md strictly.

Do not implement features not listed in v1 scope.

Specifically avoid:

AI conversation features
recommendation engines
spaced repetition logic
social features
gamification systems
complex analytics pipelines

These may be considered later.

---

## Code Modification Rules

When implementing a task:

only modify files relevant to the task
avoid refactoring unrelated code
avoid renaming packages without instruction
avoid changing architecture decisions

Keep diffs minimal and focused.

---

## Documentation Rules

When adding new components:

update relevant documentation files if necessary.

Possible files to update:

DATA_MODEL.md
API_SPEC.md
TASKS.md
DECISIONS.md

Do not rewrite documentation unnecessarily.

---

## Error Handling

Use structured exceptions.

Avoid returning raw stack traces.

Create domain-specific exceptions where helpful.

Example:

ScenarioNotFoundException
InvalidProgressUpdateException

Handle exceptions using Spring's exception handling mechanisms.

---

## Logging

Use structured logging when useful.

Avoid excessive logging.

Log meaningful events such as:

scenario retrieval failures
invalid requests
unexpected states

---

## Task Execution Strategy

Always consult TASKS.md before implementing changes.

Implement tasks incrementally.

Prefer small, reviewable changes.

If a task is ambiguous:

make minimal assumptions
choose the simplest reasonable implementation

---

## When Unsure

Prefer simple solutions.

Do not introduce new libraries unless necessary.

Ask for clarification via comments or TODO markers if needed.

---

## Development Workflow

Follow a test-driven approach where practical.

For new behavior:
1. write a failing test first
2. implement the minimum code needed to pass
3. refactor after tests pass

Do not mark a task complete unless relevant tests pass.

When adding endpoints:
- add or update integration tests
- add service-level tests when business logic exists

## Code Quality Principles

Follow clean code fundamentals:

- prefer clear and descriptive names
- keep functions small and focused
- keep classes narrowly scoped
- avoid duplicated logic
- avoid deep nesting
- keep controllers thin
- keep business logic in services
- keep repositories focused on persistence only
- prefer simple designs over clever abstractions

## Definition of Done

A task is done only when:
- implementation matches PRODUCT.md and API_SPEC.md
- relevant tests are added or updated
- all relevant tests pass
- no unrelated refactors are introduced
- documentation is updated if behavior changed## Definition of Done

A task is done only when:
- implementation matches PRODUCT.md and API_SPEC.md
- relevant tests are added or updated
- all relevant tests pass
- no unrelated refactors are introduced
- documentation is updated if behavior changed
- 
## Summary

Key priorities:

clarity
simplicity
maintainability
testability
incremental progress

Avoid unnecessary complexity.

