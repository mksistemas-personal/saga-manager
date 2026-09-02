# Skill: Delete an Entity (Soft Delete)

This skill describes the pattern and step-by-step guide to implement the delete (soft delete) use case for an entity in
the system, based on the functional architecture with `cyclops.control.Either` and conventions used in
`DeleteSagaService`.

---

## 1. Structure and Prerequisites

Before creating the delete service, make sure the following components exist or have been created:

1. **Identifier / Key (`Tsid` or equivalent primary key)**:
    - Unique identifier of the entity to be deleted.

2. **Domain Entity (`<Entity>`)**:
    - Must extend `AbstractAuditable` (which includes audit fields and soft-delete capabilities like `deleted`,
      `updatedBy`, `updatedAt`, etc.).
    - Must have a setter or mutation method `setDeleted(boolean deleted)`.
    - Must implement the `fillToUpdate()` method to record the modification timestamp and user responsible for the
      deletion.

3. **Repository Interface (`<Entity>Repository`)**:
    - Domain interface containing the methods:
        - `Optional<<Entity>> findById(Tsid id);`
        - `<Entity> save(<Entity> entity);`

4. **Business Constants (`<Domain>Constants`)**:
    - Constant with the error message when the entity is not found (e.g., `<ENTITY>_NOT_FOUND`).

---

## 2. Step-by-Step Service Implementation

### 2.1. Class Definition

- Class name: `Delete<Entity>Service`.
- Location: module service package (e.g., `app.mkiniz.sagamanager.<domain>.services`).
- Visibility: **package-private** (no `public` modifier).
- Implements interface: `DeleteBusinessUseCase<Tsid, <Entity>>`.
- Required annotations:
    - `@Service`
    - `@Transactional`
    - `@AllArgsConstructor`

### 2.2. Dependency Injection

- Declare repository as a `final` field (e.g., `private final <Entity>Repository <entity>Repository;`).
- Injection is performed via constructor generated automatically by `@AllArgsConstructor`.

### 2.3. Main Flow (`execute`)

- The `execute(Tsid id)` method orchestrates execution via functional pipeline with `Either`:
  ```java
  @Override
  public <Entity> execute(Tsid id) {
      return (<Entity>) Either.<BusinessException, Tsid>right(id)
              .flatMap(this::findById)
              .flatMap(this::save)
              .fold(this::throwBusinessException, entity -> entity);
  }
  ```

### 2.4. Pipeline Methods

Each pipeline step returns `Either<? extends BusinessException, <Entity>>`:

1. **Find by Identifier (`findById`)**:
    - Queries repository by `id`.
    - If found: returns `Either.right(entity)`.
    - If not found: returns `Either.left(new BusinessException(<Domain>Constants.<ENTITY>_NOT_FOUND))`.

2. **Soft Delete and Persistence (`save`)**:
    - Marks entity as deleted: `entity.setDeleted(true);`.
    - Invokes `entity.fillToUpdate()` to populate update audit data.
    - Persists updated entity in repository: `<entity>Repository.save(entity)`.
    - Returns `Either.right(persistedEntity)`.

---

## 3. Code Template

```java
package app.mkiniz.sagamanager.

<domain>.services;

import app.mkiniz.sagamanager.<domain>.<Domain>Constants;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>Repository;
import app.mkiniz.sagamanager.shared.business.BusinessException;
import app.mkiniz.sagamanager.shared.business.DeleteBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
class Delete<Entity>Service implements DeleteBusinessUseCase<Tsid, <Entity>>{

private final <Entity> Repository<entity> Repository;

@Override
public <Entity> execute(Tsid id) {
    return ( < Entity >)Either.<BusinessException, Tsid>right(id)
            .flatMap(this::findById)
            .flatMap(this::save)
            .fold(this::throwBusinessException, entity -> entity);
}

private Either<? extends BusinessException, <Entity>>

findById(Tsid id) {
    return <entity > Repository.findById(id)
            .map(Either::<BusinessException, < Entity >> right)
            .orElseGet(() -> Either.left(new BusinessException( < Domain > Constants.<ENTITY>_NOT_FOUND)))
}

private Either<? extends BusinessException, <Entity>>

save(<Entity>entity) {
    entity.setDeleted(true);
    entity.fillToUpdate();
    return Either.right( < entity > Repository.save(entity))

```

---

## 4. Unit Tests

Create the test class `Delete<Entity>ServiceTest` covering:

1. **Successful soft delete**:
    - Mock repository returning the existing entity in `findById`.
    - Mock repository returning the saved entity in `save`.
    - Verify that `setDeleted(true)` and `fillToUpdate()` were invoked on the entity.
    - Verify that `save` was called with the entity.
2. **Entity not found**:
    - Mock repository returning `Optional.empty()` in `findById`.
    - Validate that `BusinessException` is thrown with the expected error message (e.g.,
      `<Domain>Constants.<ENTITY>_NOT_FOUND`).
    - Verify that `save` is never invoked on the repository.
3. **Auditing**:
    - Validate that `fillToUpdate()` updates `updatedBy` and `updatedAt` with the authenticated user from
      `SecurityContextHolder` or default fallback user when unauthenticated.
