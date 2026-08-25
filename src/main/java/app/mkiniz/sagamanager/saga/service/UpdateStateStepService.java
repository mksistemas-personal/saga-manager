package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.SagaConstants;
import app.mkiniz.sagamanager.saga.domain.StateStep;
import app.mkiniz.sagamanager.saga.domain.StateStepRepository;
import app.mkiniz.sagamanager.saga.domain.StateStepRequest;
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
class UpdateStateStepService implements UpdateBusinessUseCase<Tsid, StateStepRequest, StateStep> {

    private final StateStepRepository stateStepRepository;

    @Override
    public StateStep execute(Tsid id, @Valid final StateStepRequest request) {
        Context ctx = new Context(id, request);
        return (StateStep) Either.<BusinessException, Context>right(ctx)
                .flatMap(this::findById)
                .flatMap(this::save)
                .map(context -> context.step)
                .fold(this::throwBusinessException, step -> step);
    }

    private Either<? extends BusinessException, Context> findById(Context context) {
        return stateStepRepository.findById(context.id)
                .map(step -> {
                    context.step = step;
                    return Either.<BusinessException, Context>right(context);
                })
                .orElseGet(() -> Either.left(new BusinessException(SagaConstants.STEP_NOT_FOUND)));
    }

    private Either<? extends BusinessException, Context> save(Context context) {
        context.step.setName(context.request.name());
        context.step.setDescription(context.request.description());
        context.step.setEvents(context.request.events());
        context.step.fillToUpdate();
        context.step = stateStepRepository.save(context.step);
        return Either.right(context);
    }

    private static class Context {
        public StateStep step;
        public final StateStepRequest request;
        public Tsid id;

        public Context(Tsid id, StateStepRequest request) {
            this.id = id;
            this.request = request;
        }
    }
}
