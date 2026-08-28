package app.mkiniz.sagamanager.saga.service;

import app.mkiniz.sagamanager.saga.SagaConstants;
import app.mkiniz.sagamanager.saga.UnlinkStateStepWithSagaUseCase;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
import app.mkiniz.sagamanager.shared.business.BusinessException;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
class UnlinkStateStepWithSagaService implements UnlinkStateStepWithSagaUseCase {

    private final SagaRepository sagaRepository;

    @Override
    public Tsid execute(Tsid sagaId) {
        if (sagaRepository.existsById(sagaId)) {
            sagaRepository.unlinkStateStep(sagaId);
            return sagaId;
        }
        throw new BusinessException(SagaConstants.SAGA_NOT_FOUND);
    }
}
