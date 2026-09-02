package app.mkiniz.sagamanager.saga.services;

import app.mkiniz.sagamanager.saga.SagaConstants;
import app.mkiniz.sagamanager.saga.domain.StateStep;
import app.mkiniz.sagamanager.saga.domain.StateStepRepository;
import app.mkiniz.sagamanager.shared.business.BusinessException;
import app.mkiniz.sagamanager.shared.business.DeleteBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
class DeleteStateStepService implements DeleteBusinessUseCase<Tsid, StateStep> {

    private final StateStepRepository stateStepRepository;

    @Override
    public StateStep execute(Tsid id) {
        return (StateStep) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findById)
                .flatMap(this::save)
                .fold(this::throwBusinessException, saga -> saga);
    }

    private Either<? extends BusinessException, StateStep> findById(Tsid id) {
        return stateStepRepository.findById(id)
                .map(Either::<BusinessException, StateStep>right)
                .orElseGet(() -> Either.left(new BusinessException(SagaConstants.STEP_NOT_FOUND)));
    }

    private Either<? extends BusinessException, StateStep> save(StateStep stateStep) {
        stateStep.setDeleted(true);
        stateStep.fillToUpdate();
        return Either.right(stateStepRepository.save(stateStep));
    }


}
