package app.mkiniz.sagamanager.saga.services;

import app.mkiniz.sagamanager.saga.LinkStateStepWithStateStepUseCase;
import app.mkiniz.sagamanager.saga.SagaConstants;
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
class LinkStateStepWithStateStepService implements LinkStateStepWithStateStepUseCase {
    private final StateStepRepository stateStepRepository;

    @Override
    public Tsid execute(Tsid sourceId, Tsid destId) {
        return Either.<BusinessException, Context>right(new Context(sourceId, destId))
                .flatMap(this::checkStates)
                .flatMap(this::save)
                .fold(exception -> {
                    throw (exception);
                }, stepId -> stepId);
    }

    private Either<? extends BusinessException, Tsid> save(Context context) {
        Tsid stateId = stateStepRepository.linkStateStepWithStateStep(context.sourceId(), context.destId());
        return Either.right(stateId);
    }

    private Either<BusinessException, Context> checkStates(final Context context) {
        if (!stateStepRepository.existsById(context.sourceId()))
            return Either.left(new BusinessException(SagaConstants.SOURCE_STEP_NOT_FOUND));
        if (!stateStepRepository.existsById(context.destId()))
            return Either.left(new BusinessException(SagaConstants.DEST_STEP_NOT_FOUND));
        if (stateStepRepository.existsStateStepRelationship(context.sourceId(), context.destId()))
            return Either.left(new BusinessException(SagaConstants.STEP_RELATIONSHIP_ALREADY_EXISTS));
        return Either.right(context);
    }

    private record Context(Tsid sourceId, Tsid destId) {
    }
}
