package app.mkiniz.sagamanager.shared.business;

public interface GetByIdBusinessUseCase<TKey, TResponse> extends BusinessUseCase<TKey, TResponse> {
    TResponse execute(TKey id);
}
