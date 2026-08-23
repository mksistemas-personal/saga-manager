package app.mkiniz.sagamanager.saga.domain;

import com.github.f4b6a3.tsid.Tsid;
import java.util.Optional;

public interface StateStepRepository {
    StateStep save(StateStep stateStep);
    Optional<StateStep> findById(Tsid id);
}
