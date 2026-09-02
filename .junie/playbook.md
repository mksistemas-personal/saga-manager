# Development Playbook

## New Feature

When implementing a new feature:

### Phase 1 - Understand

Inspect:

- existing architecture
- related entities
- existing services
- existing controllers
- existing tests
- database structure

Do not write code yet.

### Phase 2 - Plan

Produce:

- affected components
- proposed classes
- API changes
- database changes
- tests required

Ask for confirmation if architectural decisions are required.

### Phase 3 - Implement

Implement in small increments.

Prefer:

1. domain
2. application/service
3. repository
4. API
5. tests

### Phase 4 - Validate

Run:

- compilation
- unit tests
- integration tests when applicable

### Phase 5 - Review

Review:

- correctness
- security
- performance
- duplication
- architecture
- maintainability