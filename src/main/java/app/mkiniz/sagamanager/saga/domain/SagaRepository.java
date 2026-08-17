package app.mkiniz.sagamanager.saga.domain;

import com.github.f4b6a3.tsid.Tsid;

import java.util.Optional;

public interface SagaRepository {
    Saga save(Saga saga);

    Optional<Saga> findById(Tsid id);

}
