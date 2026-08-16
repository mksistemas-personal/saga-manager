package app.mkiniz.sagamanager.shared.business;

import jakarta.validation.Valid;

public interface UpdateBusinessUseCase<TKey, TRequest, TResponse> extends BusinessUseCase<TRequest, TResponse> {
    TResponse execute(TKey id, @Valid final TRequest request);
}
