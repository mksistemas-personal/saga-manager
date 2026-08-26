package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.LinkStateStepWithSagaUseCase;
import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRequest;
import app.mkiniz.sagamanager.saga.domain.SagaSearchRequest;
import app.mkiniz.sagamanager.shared.business.*;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Maybe;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/sagas")
@AllArgsConstructor
public class SagaController {

    private final AddBusinessUseCase<SagaRequest, Saga> addSagaUseCase;
    private final UpdateBusinessUseCase<Tsid, SagaRequest, Saga> updateSagaUseCase;
    private final DeleteBusinessUseCase<Tsid, Saga> deleteSagaUseCase;
    private final GetByIdBusinessUseCase<Tsid, Saga> getByIdSagaUseCase;
    private final GetAllBusinessUseCase<SagaSearchRequest, Maybe<Slice<Saga>>> getAllSagaUseCase;
    private final LinkStateStepWithSagaUseCase linkStateStepUseCase;

    @PostMapping
    public ResponseEntity<Saga> add(@RequestBody SagaRequest request) {
        Saga saga = addSagaUseCase.execute(request);
        return ResponseEntity
                .created(URI.create("api/sagas/" + saga.getId()))
                .body(saga);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Saga> update(@PathVariable Tsid id, @RequestBody SagaRequest request) {
        Saga saga = updateSagaUseCase.execute(id, request);
        return ResponseEntity.ok(saga);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Saga> getById(@PathVariable Tsid id) {
        Saga saga = getByIdSagaUseCase.execute(id);
        return ResponseEntity.ok(saga);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Saga> delete(@PathVariable Tsid id) {
        Saga saga = deleteSagaUseCase.execute(id);
        return ResponseEntity.ok(saga);
    }

    @GetMapping
    public ResponseEntity<Slice<Saga>> getAll(@RequestParam(required = false) String name, Pageable pageable) {
        return getAllSagaUseCase.execute(pageable, new SagaSearchRequest(name))
                .fold(ResponseEntity::ok, () -> ResponseEntity.noContent().build());
    }

    @PostMapping(path = "/link-state-step")
    public ResponseEntity<String> linkStateStep(@RequestParam Tsid sagaId, @RequestParam Tsid stateStepId) {
        Tsid response = linkStateStepUseCase.execute(sagaId, stateStepId);
        return ResponseEntity.ok(response.toString());
    }
}
