package app.mkiniz.sagamanager.saga;

import com.github.f4b6a3.tsid.Tsid;

public interface LinkStateStepWithSagaUseCase {
    Tsid execute(Tsid sagaId, Tsid stateId);
}
