package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRequest;
import app.mkiniz.sagamanager.shared.business.AddBusinessUseCase;
import app.mkiniz.sagamanager.shared.business.DeleteBusinessUseCase;
import app.mkiniz.sagamanager.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SagaControllerTest {

    private AddBusinessUseCase<SagaRequest, Saga> addSagaUseCase;
    private UpdateBusinessUseCase<Tsid, SagaRequest, Saga> updateSagaUseCase;
    private DeleteBusinessUseCase<Tsid, Saga> deleteSagaUseCase;
    private SagaController sagaController;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        addSagaUseCase = mock(AddBusinessUseCase.class);
        updateSagaUseCase = mock(UpdateBusinessUseCase.class);
        deleteSagaUseCase = mock(DeleteBusinessUseCase.class);
        sagaController = new SagaController(addSagaUseCase, updateSagaUseCase, deleteSagaUseCase);
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
        assertEquals("api/sagas/" + saga.getId(), response.getHeaders().getLocation().toString());
        assertNotNull(response.getBody());
        assertEquals(saga.getId(), response.getBody().getId());
        assertEquals(request.name(), response.getBody().getName());
        assertEquals(request.description(), response.getBody().getDescription());
        verify(addSagaUseCase).execute(any(SagaRequest.class));
    }

    @Test
    void shouldUpdateSagaAndReturnOk() {
        Tsid id = TsidCreator.getTsid();
        SagaRequest request = new SagaRequest("Saga Update Test", "Saga atualizada");
        Saga saga = Saga.builder()
                .id(id)
                .name(request.name())
                .description(request.description())
                .build();
        when(updateSagaUseCase.execute(eq(id), any(SagaRequest.class))).thenReturn(saga);

        ResponseEntity<Saga> response = sagaController.update(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(saga.getId(), response.getBody().getId());
        assertEquals(request.name(), response.getBody().getName());
        assertEquals(request.description(), response.getBody().getDescription());
        verify(updateSagaUseCase).execute(eq(id), any(SagaRequest.class));
    }

    @Test
    void shouldDeleteSagaAndReturnNoContent() {
        Tsid id = TsidCreator.getTsid();

        ResponseEntity<Saga> response = sagaController.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteSagaUseCase).execute(id);
    }
}
