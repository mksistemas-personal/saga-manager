package app.mkiniz.sagamanager.saga.services;

import app.mkiniz.sagamanager.saga.LinkStateStepWithSagaUseCase;
import app.mkiniz.sagamanager.saga.SagaConstants;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
import app.mkiniz.sagamanager.saga.domain.StateStepRepository;
import app.mkiniz.sagamanager.shared.business.BusinessException;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
class LinkStateStepWithSagaService implements LinkStateStepWithSagaUseCase {
    private final SagaRepository sagaRepository;
    private final StateStepRepository stateStepRepository;

    @Override
    public Tsid execute(Tsid sagaId, Tsid stateId) {
        return Either.<BusinessException, Context>right(new Context(sagaId, stateId))
                .flatMap(this::checkSaga)
                .flatMap(this::checkState)
                .flatMap(this::save)
                .fold(exception -> {
                    throw (exception);
                }, stepId -> stepId);
    }

    private Either<? extends BusinessException, Tsid> save(Context context) {
        Tsid stateId = sagaRepository.saveLinkRelationWithStateStep(context.sagaId(), context.stateId());
        return Either.right(stateId);
    }

    private Either<BusinessException, Context> checkState(final Context context) {
        return stateStepRepository.findById(context.stateId())
                .map(step -> Either.<BusinessException, Context>right(context))
                .orElseGet(() -> Either.left(new BusinessException(SagaConstants.STEP_NOT_FOUND)));
    }

    private Either<BusinessException, Context> checkSaga(Context context) {
        return sagaRepository.findById(context.sagaId())
                .map(saga -> Either.<BusinessException, Context>right(context))
                .orElseGet(() -> Either.left(new BusinessException(SagaConstants.SAGA_NOT_FOUND)));
    }

    private record Context(Tsid sagaId, Tsid stateId) {
    }
}
