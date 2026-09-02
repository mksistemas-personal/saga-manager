package app.mkiniz.sagamanager.saga.services;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
import app.mkiniz.sagamanager.saga.domain.SagaRequest;
import app.mkiniz.sagamanager.shared.business.AddBusinessUseCase;
import app.mkiniz.sagamanager.shared.business.BusinessException;
import cyclops.control.Either;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@AllArgsConstructor
@Validated
class AddSagaService implements AddBusinessUseCase<SagaRequest, Saga> {

    private final SagaRepository sagaRepository;

    @Override
    public Saga execute(@Valid SagaRequest request) {
        return (Saga) Either.<BusinessException, Context>right(new Context(request))
                .flatMap(this::create)
                .flatMap(this::save)
                .map(context -> context.saga)
                .fold(this::throwBusinessException, saga -> saga);
    }

    private Either<? extends BusinessException, ? extends Context> create(Context context) {
        context.saga = Saga.builder()
                .name(context.request.name())
                .description(context.request.description())
                .build();
        context.saga.fillToCreate();
        return Either.right(context);
    }

    private Either<? extends BusinessException, ? extends Context> save(Context context) {
        sagaRepository.save(context.saga);
        return Either.right(context);
    }

    private static class Context {
        public Saga saga;
        public final SagaRequest request;

        public Context(SagaRequest request) {
            this.request = request;
        }
    }
}
