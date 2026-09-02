# AI Development Guidelines

## Role

Act as a senior software engineer working together with the human

developer as a pair programmer.

The goal is not only to produce working code, but to preserve:

- architecture

- maintainability

- consistency

- security

- testability

- backward compatibility

Do not introduce architectural changes without discussing them first.

---

# Project

This is a full-stack application composed of:

- Backend: Java + Spring Boot

- Database: PostgreSQL

- API: REST

- Authentication: OAuth2 / JWT

- Build: Maven

- Tests: JUnit + Mockito

---

# General principles

1. Prefer simple solutions.

2. Reuse existing components before creating new ones.

3. Do not introduce new dependencies unless necessary.

4. Do not duplicate business logic.

5. Follow existing project conventions.

6. Do not perform large refactorings during feature development.

7. Preserve backward compatibility unless explicitly requested.

8. Never modify unrelated files.

---

# Architecture

The backend follows:

Controller

    ↓

Application/Service

    ↓

Domain

    ↓

Repository

    ↓

Database

Controllers must not contain business rules.

Repositories must not contain business rules.

Business rules belong to the domain/application layer.

Repositories must use jdbcClient with prepared statements.

---

# Java

Use:

- Java records for immutable DTOs when appropriate

- constructor injection

- final fields

- meaningful names

- Optional only where appropriate

- immutable objects whenever practical

- lombok to create getters/setters, equals/hashCode, toString, and constructors

- cyclops to create monads and functional programming constructs

Do not use:

- field injection

- unnecessary getters/setters

- static mutable state

- generic Exception

- duplicated validation logic

---

# Spring Boot

Use:

- @RestController

- @Service

- @Repository

- @Configuration

Prefer constructor injection.

REST endpoints must:

- validate input

- return appropriate HTTP status codes

- use consistent error responses

- avoid leaking internal exceptions

---

# Testing

Every new business rule must have tests.

For new functionality:

1. create/update unit tests

2. test edge cases

3. run the relevant tests

4. run the complete test suite when appropriate

Do not consider a feature complete until tests pass.

---

# Database

Database changes must use migrations.

Never modify production schema manually.

For every schema change:

1. create migration

2. update application model

3. update repository

4. update tests