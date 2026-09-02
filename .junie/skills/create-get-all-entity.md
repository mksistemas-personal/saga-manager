# Skill: Retrieve an Entity Collection (Paged/Sliced)

This skill describes the pattern and step-by-step guide to implement the retrieve-all (collection query with
pagination/slice) use case for an entity in the system, based on the functional architecture with
`cyclops.control.Maybe` and `cyclops.control.Eval` as used in `GetAllSagaService`.

---

## 1. Structure and Prerequisites

Before creating the retrieve-all service, make sure the following components exist or have been created:

1. **Search Request DTO (`<Entity>SearchRequest`)**:
    - Record or class containing optional search/filter criteria (can be `@Nullable`).

2. **Domain Entity (`<Entity>`)**:
    - Domain entity class representing the entity.

3. **Repository Interface (`<Entity>Repository`)**:
    - Domain interface containing the method:
        - `Slice<<Entity>> findBySearchRequest(@Nullable <Entity>SearchRequest request, Pageable pageable);`

4. **Spring Data Pagination Types**:
    - `org.springframework.data.domain.Pageable`
    - `org.springframework.data.domain.Slice`
    - `org.springframework.data.domain.SliceImpl`

---

## 2. Step-by-Step Service Implementation

### 2.1. Class Definition

- Class name: `GetAll<Entity>Service`.
- Location: module service package (e.g., `app.mkiniz.sagamanager.<domain>.services`).
- Visibility: **package-private** (no `public` modifier).
- Implements interface: `GetAllBusinessUseCase<<Entity>SearchRequest, Maybe<Slice<<Entity>>>>`.
- Required annotations:
    - `@Service`
    - `@Transactional(readOnly = true)`
    - `@AllArgsConstructor`

### 2.2. Dependency Injection

- Declare repository as a `final` field (e.g., `private final <Entity>Repository <entity>Repository;`).
- Injection is performed via constructor generated automatically by `@AllArgsConstructor`.

### 2.3. Main Flow (`execute`)

- The `execute(Pageable pageable, @Nullable <Entity>SearchRequest request)` method evaluates the repository query lazily
  and wraps it into a `Maybe<Slice<<Entity>>>`:
  ```java
  @Override
  public Maybe<Slice<<Entity>>> execute(Pageable pageable, @Nullable <Entity>SearchRequest request) {
      return Maybe.fromEval(later(() -> <entity>Repository.findBySearchRequest(request, pageable)))
              .filter(Slice::hasContent)
              .map(items ->
                      new SliceImpl<>(items.stream().toList(),
                              pageable,
                              items.hasNext()));
  }
  ```

### 2.4. Functional Flow Explanation

1. **Lazy Evaluation (`Maybe.fromEval(later(...))` )**:
    - Defers repository execution until evaluated by `Maybe`.
2. **Filtering Empty Results (`filter(Slice::hasContent)`)**:
    - If the retrieved slice is empty (`!hasContent()`), returns `Maybe.nothing()` (empty `Maybe`).
3. **Slice Transformation (`map(...)`)**:
    - Reconstructs a clean `SliceImpl` containing the extracted elements list, the original `Pageable`, and the
      `hasNext()` indicator.

---

## 3. Code Template

```java
package app.mkiniz.sagamanager.<domain>.services;

import app.mkiniz.sagamanager.<domain>.domain.<Entity>;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>Repository;
import app.mkiniz.sagamanager.<domain>.domain.<Entity>SearchRequest;
import app.mkiniz.sagamanager.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAll<Entity>Service implements GetAllBusinessUseCase<<Entity>SearchRequest, Maybe<Slice<<Entity>>>> {

    private final <Entity>Repository <entity>Repository;

    @Override
    public Maybe<Slice<<Entity>>> execute(Pageable pageable, @Nullable <Entity>SearchRequest request) {
        return Maybe.fromEval(later(() -> <entity>Repository.findBySearchRequest(request, pageable)))
                .filter(Slice::hasContent)
                .map(items ->
                        new SliceImpl<>(items.stream().toList(),
                                pageable,
                                items.hasNext()));
    }
}
```

---

## 4. Unit Tests

Create the test class `GetAll<Entity>ServiceTest` covering:

1. **Successful retrieval with content**:
    - Mock repository returning a `Slice` with elements for `findBySearchRequest(request, pageable)`.
    - Execute `service.execute(pageable, request)`.
    - Assert that `Maybe` is present (`isPresent()` is true).
    - Assert that the returned `Slice` has content and matches the expected list.
    - Verify that `<entity>Repository.findBySearchRequest(request, pageable)` was called once.
2. **Empty result**:
    - Mock repository returning an empty `Slice` (`Slice.empty()` or `hasContent() == false`).
    - Execute `service.execute(pageable, request)`.
    - Assert that `Maybe` is empty (`isEmpty()` or `isPresent() == false`).
    - Verify that `<entity>Repository.findBySearchRequest(request, pageable)` was called once.
