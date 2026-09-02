package app.mkiniz.sagamanager.process.domain;

import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SagaProcess {
    private Tsid id;
    private String name;
    private StateStepProcess startStep;

}
