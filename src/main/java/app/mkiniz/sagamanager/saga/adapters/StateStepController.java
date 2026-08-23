package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.StateStep;
import app.mkiniz.sagamanager.saga.domain.StateStepRequest;
import app.mkiniz.sagamanager.shared.business.AddBusinessUseCase;
import app.mkiniz.sagamanager.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/state-steps")
@AllArgsConstructor
public class StateStepController {

    private final AddBusinessUseCase<StateStepRequest, StateStep> addStateStepUseCase;
    private final UpdateBusinessUseCase<Tsid, StateStepRequest, StateStep> updateStepStepUseCase;

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
}
