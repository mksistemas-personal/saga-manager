# Skill: Update an Entity

This skill describes the pattern and step-by-step guide to implement the update use case for an existing entity in the
system, based on the functional architecture with `cyclops.control.Either` and conventions used in `UpdateSagaService`.

---

## 1. Structure and Prerequisites

Before creating the update service, make sure the following components exist or have been created:

1. **Identifier / Key (`Tsid` or equivalent primary key)**:
    - Unique identifier of the entity to be updated.

2. **Request DTO (`<Entity>Request`)**:
    - Immutable record with the data to be updated.
    - Bean Validation annotations (`@NotBlank`, `@NotNull`, etc.), if applicable.

3. **Domain Entity (`<Entity>`)**:
    - Must extend `AbstractAuditable` (for audit fields such as `updatedBy`, `updatedAt`, etc.).
    - Must have setters (or mutation/business methods) for modifiable fields.
    - Must implement the `fillToUpdate()` method to record the update timestamp and the user responsible for the
      modification.

4. **Repository Interface (`<Entity>Repository`)**:
    - Domain interface containing the methods:
        - `Optional<<Entity>> findById(Tsid id);`
        - `<Entity> save(<Entity> entity);`

5. **Business Constants (`<Domain>Constants`)**:
    - Constant with the error message when the entity is not found (e.g., `<ENTITY>_NOT_FOUND`).

---

## 2. Step-by-Step Service Implementation

### 2.1. Class Definition

- Class name: `Update<Entity>Service`.
- Location: module service package (e.g., `app.mkiniz.sagamanager.<domain>.services`).
- Visibility: **package-private** (no `public` modifier).
- Implements interface: `UpdateBusinessUseCase<Tsid, <Entity>Request, <Entity>>`.
- Required annotations:
    - `@Service`
    - `@Transactional`
    - `@AllArgsConstructor`
    - `@Validated`

### 2.2. Dependency Injection

- Declare repository as a `final` field (e.g., `private final <Entity>Repository <entity>Repository;`).
- Injection is performed via constructor generated automatically by `@AllArgsConstructor`.

### 2.3. Internal Context Class (`Context`)

- Create a private static inner class `Context` to pass state between functional pipeline steps:
  ```java
  private static class Context {
      public <Entity> entity;
      public final <Entity>Request request;
      public Tsid id;

      public Context(<Entity>Request request) {
          this.request = request;
      }

      public Context(Tsid id, <Entity>Request request) {
          this.id = id;
          this.request = request;
      }
  }
  ```

### 2.4. Main Flow (`execute`)

- The `execute(Tsid id, @Valid final <Entity>Request request)` method orchestrates execution via functional pipeline
  with `Either`:
  ```java
  @Override
  public <Entity> execute(Tsid id, @Valid final <Entity>Request request) {
      Context ctx = new Context(id, request);
      return (<Entity>) Either.<BusinessException, Context>right(ctx)
              .flatMap(this::findById)
              .flatMap(this::save)
              .map(context -> context.entity)
              .fold(this::throwBusinessException, entity -> entity);
  }
  ```

### 2.5. Pipeline Methods

Each pipeline step receives `Context` and returns `Either<? extends BusinessException, Context>`:

1. **Find by Identifier (`findById`)**:
    - Queries repository by `context.id`.
    - If found: assigns entity to `context.entity` and returns `Either.right(context)`.
    - If not found: returns `Either.left(new BusinessException(<Domain>Constants.<ENTITY>_NOT_FOUND))`.

2. **Modification and Persistence (`save`)**:
    - Updates existing entity fields from DTO data (`context.request`).
    - Invokes `context.entity.fillToUpdate()` to populate update audit data.
    - Persists entity in repository: `context.entity = <entity>Repository.save(context.entity);`.
    - Returns `Either.right(context)`.

---

## 3. Code Template

```java
package app.mkiniz.sagamanager.

<domain>.services;

import app.mkiniz.sagamanager.<domain>.<Domain>Constants;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>Repository;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>Request;
import app.mkiniz.sagamanager.shared.business.BusinessException;
import app.mkiniz.sagamanager.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@AllArgsConstructor
@Validated
class Update<Entity>Service implements UpdateBusinessUseCase<Tsid, <Entity>Request, <Entity>>{

private final <Entity> Repository<entity> Repository;

@Override
public <Entity> execute(Tsid id, @Valid final <Entity>Request request) {
    Context ctx = new Context(id, request);
    return ( < Entity >)Either.<BusinessException, Context>right(ctx)
            .flatMap(this::findById)
            .flatMap(this::save)
            .map(context -> context.entity)
            .fold(this::throwBusinessException, entity -> entity);
}

private Either<? extends BusinessException, Context> findById(Context context) {
    return <entity > Repository.findById(context.id)
            .map(entity -> {
                context.entity = entity;
                return Either.<BusinessException, Context>right(context);
            })
            .orElseGet(() -> Either.left(new BusinessException( < Domain > Constants.<ENTITY>_NOT_FOUND)))
}

private Either<? extends BusinessException, Context> save(Context context) {
    context.entity.setName(context.request.name());
    context.entity.setDescription(context.request.description());
    context.entity.fillToUpdate();
    context.entity = < entity > Repository.save(context.entity);
    return Either.right(context);
}

private static class Context {
    public <Entity>entity;
    public final <Entity> Request request;
    public Tsid id;

    public Context(Tsid id, <Entity>Request request) {
        this.id = id;
        this.request = request;
    }
}
}
```

---

## 4. Unit Tests

Create the test class `Update<Entity>ServiceTest` covering:

1. **Successful update**:
    - Mock repository returning the existing entity in `findById`.
    - Mock repository returning the updated entity in `save`.
    - Verify mock invocations and field modifications.
2. **Entity not found**:
    - Mock repository returning `Optional.empty()` in `findById`.
    - Validate that `BusinessException` is thrown with the expected message.
    - Verify that the repository's `save` method is never called.
3. **Update Auditing**:
    - Validate that `fillToUpdate()` updates `updatedBy` and `updatedAt` with the authenticated user from
      `SecurityContextHolder` or default fallback user when unauthenticated.
