package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
import app.mkiniz.sagamanager.saga.domain.SagaSearchRequest;
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
class GetAllSagaService implements GetAllBusinessUseCase<SagaSearchRequest, Maybe<Slice<Saga>>> {

    private final SagaRepository sagaRepository;

    @Override
    public Maybe<Slice<Saga>> execute(Pageable pageable, @Nullable SagaSearchRequest request) {
        return Maybe.fromEval(later(() -> sagaRepository.findBySearchRequest(request, pageable)))
                .filter(Slice::hasContent)
                .map(sagas ->
                        new SliceImpl<>(sagas.stream().toList(),
                                pageable,
                                sagas.hasNext()));
    }
}
