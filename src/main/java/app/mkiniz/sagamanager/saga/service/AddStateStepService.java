package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.domain.SingleStateStep;
import app.mkiniz.sagamanager.saga.domain.StateStep;
import app.mkiniz.sagamanager.saga.domain.StateStepRepository;
import app.mkiniz.sagamanager.saga.domain.StateStepRequest;
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
class AddStateStepService implements AddBusinessUseCase<StateStepRequest, StateStep> {

    private final StateStepRepository stateStepRepository;

    @Override
    public StateStep execute(@Valid StateStepRequest request) {
        return (StateStep) Either.<BusinessException, Context>right(new Context(request))
                .flatMap(this::create)
                .flatMap(this::save)
                .map(context -> context.step)
                .fold(this::throwBusinessException, step -> step);
    }

    private Either<? extends BusinessException, ? extends Context> create(Context context) {
        context.step = SingleStateStep.builder()
                .name(context.request.name())
                .description(context.request.description())
                .events(context.request.events())
                .build();
        context.step.fillToCreate();
        return Either.right(context);
    }

    private Either<? extends BusinessException, ? extends Context> save(Context context) {
        context.step = stateStepRepository.save(context.step);
        return Either.right(context);
    }

    private static class Context {
        public StateStep step;
        public final StateStepRequest request;

        public Context(StateStepRequest request) {
            this.request = request;
        }
    }
}
