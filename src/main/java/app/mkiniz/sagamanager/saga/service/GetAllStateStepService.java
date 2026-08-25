package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.domain.StateStep;
import app.mkiniz.sagamanager.saga.domain.StateStepRepository;
import app.mkiniz.sagamanager.saga.domain.StateStepSearchRequest;
import app.mkiniz.sagamanager.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class GetAllStateStepService implements GetAllBusinessUseCase<StateStepSearchRequest, Maybe<Slice<StateStep>>> {

    private final StateStepRepository stateStepRepository;

    @Override
    public Maybe<Slice<StateStep>> execute(Pageable pageable, @Nullable StateStepSearchRequest stateStepSearchRequest) {
        return Maybe.fromEval(later(() -> stateStepRepository.findBySearchRequest(stateStepSearchRequest, pageable)))
                .filter(Slice::hasContent)
                .map(stateSteps ->
                        new SliceImpl<>(stateSteps.stream().toList(),
                                pageable,
                                stateSteps.hasNext()));
    }
}
