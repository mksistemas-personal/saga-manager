package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.SagaConstants;
import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
import app.mkiniz.sagamanager.saga.domain.SagaRequest;
import app.mkiniz.sagamanager.shared.business.BusinessException;
import app.mkiniz.sagamanager.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
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
class UpdateSagaService implements UpdateBusinessUseCase<Tsid, SagaRequest, Saga> {

    private final SagaRepository sagaRepository;

    @Override
    public Saga execute(Tsid id, @Valid final SagaRequest sagaRequest) {
        Context ctx = new Context(sagaRequest);
        ctx.id = id;
        return (Saga) Either.<BusinessException, Context>right(ctx)
                .flatMap(this::findById)
                .flatMap(this::save)
                .map(context -> context.saga)
                .fold(this::throwBusinessException, saga -> saga);
    }

    private Either<? extends BusinessException, Context> findById(Context context) {
        return sagaRepository.findById(context.id)
                .map(saga -> {
                    context.saga = saga;
                    return Either.<BusinessException, Context>right(context);
                })
                .orElseGet(() -> Either.left(new BusinessException(SagaConstants.SAGA_NOT_FOUND)));
    }

    private Either<? extends BusinessException, Context> save(Context context) {
        context.saga.setName(context.request.name());
        context.saga.setDescription(context.request.description());
        context.saga.fillToUpdate();
        context.saga = sagaRepository.save(context.saga);
        return Either.right(context);
    }

    private static class Context {
        public Saga saga;
        public final SagaRequest request;
        public Tsid id;

        public Context(SagaRequest request) {
            this.request = request;
        }
    }
}
