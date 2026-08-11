package app.mkiniz.sagamanager.shared.business;

import jakarta.validation.Valid;

public interface AddBusinessUseCase<TRequest, TResponse> extends BusinessUseCase<TRequest, TResponse> {
    TResponse execute(@Valid TRequest request);
}
