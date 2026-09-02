# Skill: Retrieve an Entity by ID

This skill describes the pattern and step-by-step guide to implement the retrieve-by-ID use case for an entity in the
system, based on the functional architecture with `cyclops.control.Either` and conventions used in `GetByIdSagaService`.

---

## 1. Structure and Prerequisites

Before creating the retrieve-by-ID service, make sure the following components exist or have been created:

1. **Identifier / Key (`Tsid` or equivalent primary key)**:
    - Unique identifier of the entity to be retrieved.

2. **Domain Entity (`<Entity>`)**:
    - Domain entity class representing the entity.

3. **Repository Interface (`<Entity>Repository`)**:
    - Domain interface containing the method:
        - `Optional<<Entity>> findById(Tsid id);`

4. **Business Constants (`<Domain>Constants`)**:
    - Constant with the error message when the entity is not found (e.g., `<ENTITY>_NOT_FOUND`).

---

## 2. Step-by-Step Service Implementation

### 2.1. Class Definition

- Class name: `GetById<Entity>Service`.
- Location: module service package (e.g., `app.mkiniz.sagamanager.<domain>.services`).
- Visibility: **package-private** (no `public` modifier).
- Implements interface: `GetByIdBusinessUseCase<Tsid, <Entity>>`.
- Required annotations:
    - `@Service`
    - `@Transactional(readOnly = true)`
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
              .fold(this::throwBusinessException, entity -> entity);
  }
  ```

### 2.4. Pipeline Methods

Each pipeline step returns `Either<? extends BusinessException, <Entity>>`:

1. **Find by Identifier (`findById`)**:
    - Queries repository by `id`.
    - If found: returns `Either.right(entity)`.
    - If not found: returns `Either.left(new BusinessException(<Domain>Constants.<ENTITY>_NOT_FOUND))`.

---

## 3. Code Template

```java
package app.mkiniz.sagamanager.

<domain>.services;

import app.mkiniz.sagamanager.<domain>.<Domain>Constants;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>Repository;
import app.mkiniz.sagamanager.shared.business.BusinessException;
import app.mkiniz.sagamanager.shared.business.GetByIdBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetById<Entity>Service implements GetByIdBusinessUseCase<Tsid, <Entity>>{

private final <Entity> Repository<entity> Repository;

@Override
public <Entity> execute(Tsid id) {
    return ( < Entity >)Either.<BusinessException, Tsid>right(id)
            .flatMap(this::findById)
            .fold(this::throwBusinessException, entity -> entity);
}

private Either<? extends BusinessException, <Entity>>

findById(Tsid id) {
    return <entity > Repository.findById(id)
            .map(Either::<BusinessException, < Entity >> right)
            .orElseGet(() -> Either.left(new BusinessException( < Domain > Constants.<ENTITY>_NOT_FOUND)))
}
}
```

---

## 4. Unit Tests

Create the test class `GetById<Entity>ServiceTest` covering:

1. **Successful retrieval**:
    - Mock repository returning `Optional.of(entity)` in `findById(id)`.
    - Execute `service.execute(id)`.
    - Assert that the returned entity matches the expected one.
    - Verify that `<entity>Repository.findById(id)` was called once.
2. **Entity not found**:
    - Mock repository returning `Optional.empty()` in `findById(id)`.
    - Validate that `BusinessException` is thrown with the expected error message (e.g.,
      `<Domain>Constants.<ENTITY>_NOT_FOUND`).
    - Verify that `<entity>Repository.findById(id)` was called once.
