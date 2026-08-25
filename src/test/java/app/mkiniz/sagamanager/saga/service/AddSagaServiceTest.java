package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
import app.mkiniz.sagamanager.saga.domain.SagaRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AddSagaServiceTest {

    private SagaRepository sagaRepository;
    private AddSagaService addSagaService;

    @BeforeEach
    void setUp() {
        sagaRepository = mock(SagaRepository.class);
        when(sagaRepository.save(any(Saga.class))).thenAnswer(invocation -> invocation.getArgument(0));
        addSagaService = new AddSagaService(sagaRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateSagaWithCurrentUser() {
        // Arrange
        String username = "test-user";
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(username, "pass", Collections.emptyList()));
        SecurityContextHolder.setContext(securityContext);

        SagaRequest request = new SagaRequest("Saga Test", "Description Test");

        // Act
        Saga result = addSagaService.execute(request);

        // Assert
        assertNotNull(result);
        assertEquals("Saga Test", result.getName());
        assertEquals(username, result.getCreatedBy());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(sagaRepository).save(any(Saga.class));
    }

    @Test
    void shouldCreateSagaWithSystemUserWhenNotAuthenticated() {
        // Arrange
        SecurityContextHolder.clearContext();
        SagaRequest request = new SagaRequest("Saga Test", "Description Test");

        // Act
        Saga result = addSagaService.execute(request);

        // Assert
        assertNotNull(result);
        assertEquals("system", result.getCreatedBy());
        assertEquals("system", result.getUpdatedBy());
        verify(sagaRepository).save(any(Saga.class));
    }
}
