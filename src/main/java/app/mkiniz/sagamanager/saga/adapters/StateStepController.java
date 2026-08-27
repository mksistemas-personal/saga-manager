package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.LinkStateStepToCompositeStepUseCase;
import app.mkiniz.sagamanager.saga.domain.StateStep;
import app.mkiniz.sagamanager.saga.domain.StateStepRequest;
import app.mkiniz.sagamanager.saga.domain.StateStepSearchRequest;
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
@RequestMapping("api/state-steps")
@AllArgsConstructor
public class StateStepController {

    private final AddBusinessUseCase<StateStepRequest, StateStep> addStateStepUseCase;
    private final UpdateBusinessUseCase<Tsid, StateStepRequest, StateStep> updateStepStepUseCase;
    private final DeleteBusinessUseCase<Tsid, StateStep> deleteSagaUseCase;
    private final GetByIdBusinessUseCase<Tsid, StateStep> getByIdStateStepUseCase;
    private final GetAllBusinessUseCase<StateStepSearchRequest, Maybe<Slice<StateStep>>> getAllStateStepUseCase;
    private final LinkStateStepToCompositeStepUseCase linkStateStepToCompositeStepUseCase;

    @PostMapping
    public ResponseEntity<StateStep> add(@RequestBody StateStepRequest request) {
        StateStep stateStep = addStateStepUseCase.execute(request);
        return ResponseEntity
                .created(URI.create("api/state-steps/" + stateStep.getId()))
                .body(stateStep);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<StateStep> update(@PathVariable Tsid id, @RequestBody StateStepRequest request) {
        StateStep stateStep = updateStepStepUseCase.execute(id, request);
        return ResponseEntity.ok(stateStep);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<StateStep> delete(@PathVariable Tsid id) {
        StateStep stateStep = deleteSagaUseCase.execute(id);
        return ResponseEntity.ok(stateStep);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<StateStep> getById(@PathVariable Tsid id) {
        StateStep stateStep = getByIdStateStepUseCase.execute(id);
        return ResponseEntity.ok(stateStep);
    }

    @GetMapping
    public ResponseEntity<Slice<StateStep>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String event,
            Pageable pageable) {
        return getAllStateStepUseCase.execute(pageable, new StateStepSearchRequest(name, event))
                .fold(ResponseEntity::ok, () -> ResponseEntity.noContent().build());
    }

    @PostMapping(path = "/{id}/add-child-composite")
    public ResponseEntity<Void> getComposite(@RequestParam Tsid ownerId, @RequestParam Tsid childId) {
        linkStateStepToCompositeStepUseCase.execute(ownerId, childId);
        return ResponseEntity.ok().build();
    }
}
