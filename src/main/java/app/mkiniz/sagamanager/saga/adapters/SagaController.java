package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRequest;
import app.mkiniz.sagamanager.shared.business.AddBusinessUseCase;
import app.mkiniz.sagamanager.shared.business.DeleteBusinessUseCase;
import app.mkiniz.sagamanager.shared.business.GetByIdBusinessUseCase;
import app.mkiniz.sagamanager.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
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

    @PostMapping
    public ResponseEntity<Saga> add(@RequestBody SagaRequest request) {
        Saga saga = addSagaUseCase.execute(request);
        return ResponseEntity
                .created(URI.create("api/sagas/" + saga.getId()))
                .body(saga);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Saga> update(@PathVariable Tsid id, @RequestBody SagaRequest request) {
        Saga saga = updateSagaUseCase.execute(id, request);
        return ResponseEntity.ok(saga);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Saga> getById(@PathVariable Tsid id) {
        Saga saga = getByIdSagaUseCase.execute(id);
        return ResponseEntity.ok(saga);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Saga> delete(@PathVariable Tsid id) {
        Saga saga = deleteSagaUseCase.execute(id);
        return ResponseEntity.ok(saga);
    }
}
