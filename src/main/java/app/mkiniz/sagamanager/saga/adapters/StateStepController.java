package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.StateStep;
import app.mkiniz.sagamanager.saga.domain.StateStepRequest;
import app.mkiniz.sagamanager.shared.business.AddBusinessUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("api/state-steps")
@AllArgsConstructor
public class StateStepController {

    private final AddBusinessUseCase<StateStepRequest, StateStep> addStateStepUseCase;

    @PostMapping
    public ResponseEntity<StateStep> add(@RequestBody StateStepRequest request) {
        StateStep stateStep = addStateStepUseCase.execute(request);
        return ResponseEntity
                .created(URI.create("api/state-steps/" + stateStep.getId()))
                .body(stateStep);
    }
}
