package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRequest;
import app.mkiniz.sagamanager.shared.business.AddBusinessUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/sagas")
@AllArgsConstructor
public class SagaController {

    private final AddBusinessUseCase<SagaRequest, Saga> addSagaUseCase;

    @PostMapping
    public ResponseEntity<Saga> add(@RequestBody SagaRequest request) {
        Saga saga = addSagaUseCase.execute(request);
        return ResponseEntity
                .created(URI.create("/sagas/" + saga.getId()))
                .body(saga);
    }
}
