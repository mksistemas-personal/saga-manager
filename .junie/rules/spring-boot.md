# Spring Boot Rules

## Controllers

Controllers are responsible only for:

- HTTP concerns
- input validation
- mapping requests
- mapping responses

Never implement business rules inside controllers.

## Services

Services contain application orchestration.

Business rules should be delegated to domain objects when appropriate.

## Dependency Injection

Always use constructor injection.

Never use @Autowired on fields.