# ADR-004: Distributed Cache

## Decision

Use Redis for distributed cache.

## Context

The application runs multiple Spring Boot instances.

Local in-memory cache is insufficient because cache state must be shared.

## Consequences

Positive:

- shared cache
- horizontal scalability

Negative:

- additional infrastructure
- cache invalidation complexity