package app.mkiniz.sagamanager.shared.business;

public interface DeleteBusinessUseCase<TKey, TResponse> extends BusinessUseCase<TKey, TResponse> {
    TResponse execute(TKey id);
}
