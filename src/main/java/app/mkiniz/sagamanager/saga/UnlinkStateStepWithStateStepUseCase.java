package app.mkiniz.sagamanager.saga;

import com.github.f4b6a3.tsid.Tsid;

public interface UnlinkStateStepWithStateStepUseCase {
    Tsid execute(Tsid sourceId, Tsid destId);
}
