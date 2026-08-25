package app.mkiniz.sagamanager.saga.domain;

import app.mkiniz.sagamanager.saga.SagaConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StateStepRequest(
        @NotNull(message = SagaConstants.STEP_NAME_NOT_NULL)
        @NotBlank(message = SagaConstants.STEP_NAME_NOT_BLANK)
        String name,
        String description,
        List<String> events) {
}
