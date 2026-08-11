package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRequest;
import app.mkiniz.sagamanager.shared.business.AddBusinessUseCase;
import com.github.f4b6a3.tsid.TsidCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SagaControllerTest {

    private AddBusinessUseCase<SagaRequest, Saga> addSagaUseCase;
    private SagaController sagaController;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        addSagaUseCase = mock(AddBusinessUseCase.class);
        sagaController = new SagaController(addSagaUseCase);
    }

    @Test
    void shouldAddSagaAndReturnCreated() {
        SagaRequest request = new SagaRequest("Saga Test", "Saga de teste");
        Saga saga = Saga.builder()
                .id(TsidCreator.getTsid())
                .name(request.name())
                .description(request.description())
                .build();
        when(addSagaUseCase.execute(any(SagaRequest.class))).thenReturn(saga);

        ResponseEntity<Saga> response = sagaController.add(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertEquals("/sagas/" + saga.getId(), response.getHeaders().getLocation().toString());
        assertNotNull(response.getBody());
        assertEquals(saga.getId(), response.getBody().getId());
        assertEquals(request.name(), response.getBody().getName());
        assertEquals(request.description(), response.getBody().getDescription());
        verify(addSagaUseCase).execute(any(SagaRequest.class));
    }
}
