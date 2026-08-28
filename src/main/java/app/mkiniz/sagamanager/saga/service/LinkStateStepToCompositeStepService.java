package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.LinkStateStepToCompositeStepUseCase;
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
class LinkStateStepToCompositeStepService implements LinkStateStepToCompositeStepUseCase {

    private final StateStepRepository stateStepRepository;

    @Override
    public void execute(Tsid ownerId, Tsid childId) {
        Either.<BusinessException, Context>right(new Context(ownerId, childId))
                .flatMap(this::checkStateSteps)
                .flatMap(this::save)
                .fold(exception -> {
                    throw (exception);
                }, context -> null);
    }

    private Either<? extends BusinessException, Context> save(Context context) {
        stateStepRepository.linkChildToComposite(context.ownerId(), context.childId());
        return Either.right(context);
    }

    private Either<? extends BusinessException, Context> checkStateSteps(Context context) {
        if (!stateStepRepository.existsById(context.ownerId()))
            return Either.left(new BusinessException(SagaConstants.OWNER_STEP_NOT_FOUND));
        if (!stateStepRepository.existsById(context.childId()))
            return Either.left(new BusinessException(SagaConstants.CHILD_STEP_NOT_FOUND));
        if (stateStepRepository.existsCompositeLink(context.ownerId(), context.childId()))
            return Either.left(new BusinessException(SagaConstants.CHILD_COMPOSITE_FOUND));
        return Either.right(context);
    }

    private record Context(Tsid ownerId, Tsid childId) {
    }
}
