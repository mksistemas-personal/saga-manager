package app.mkiniz.sagamanager.saga.domain;

import com.github.f4b6a3.tsid.Tsid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface SagaRepository {
    Saga save(Saga saga);

    Optional<Saga> findById(Tsid id);

    Slice<Saga> findBySearchRequest(SagaSearchRequest request, Pageable pageable);

    Tsid saveLinkRelationWithStateStep(Tsid sagaId, Tsid stateStepId);

    void unlinkStateStep(Tsid sagaId);

    boolean existsById(Tsid sagaId);
}
