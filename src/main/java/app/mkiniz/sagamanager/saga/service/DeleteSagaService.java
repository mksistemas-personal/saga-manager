package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.SagaConstants;
import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
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
class DeleteSagaService implements DeleteBusinessUseCase<Tsid, Saga> {

    private final SagaRepository sagaRepository;

    @Override
    public Saga execute(Tsid id) {
        return (Saga) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findById)
                .flatMap(this::save)
                .fold(this::throwBusinessException, saga -> saga);
    }

    private Either<? extends BusinessException, Saga> findById(Tsid id) {
        return sagaRepository.findById(id)
                .map(Either::<BusinessException, Saga>right)
                .orElseGet(() -> Either.left(new BusinessException(SagaConstants.SAGA_NOT_FOUND)));
    }

    private Either<? extends BusinessException, Saga> save(Saga saga) {
        saga.setDeleted(true);
        saga.fillToUpdate();
        return Either.right(sagaRepository.save(saga));
    }


}
