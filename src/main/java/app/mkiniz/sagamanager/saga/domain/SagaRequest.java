package app.mkiniz.sagamanager.saga.domain;

import app.mkiniz.sagamanager.saga.SagaConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SagaRequest(
        @NotNull(message = SagaConstants.NAME_NOT_NULL)
        @NotBlank(message = SagaConstants.NAME_NOT_BLANK)
        String name,
        String description) {
}
