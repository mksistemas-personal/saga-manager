package app.mkiniz.sagamanager.saga.domain;

import com.github.f4b6a3.tsid.Tsid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface StateStepRepository {
    StateStep save(StateStep stateStep);

    Optional<StateStep> findById(Tsid id);

    Slice<StateStep> findBySearchRequest(StateStepSearchRequest request, Pageable pageable);

    boolean existsById(Tsid id);

    void linkChildToComposite(Tsid ownerId, Tsid childId);

    boolean existsCompositeLink(Tsid ownerId, Tsid childId);

    void unlinkChildToComposite(Tsid ownerId, Tsid childId);
}
