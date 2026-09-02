package app.mkiniz.sagamanager.process.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SagaInfraStructure {
    private Set<SagaProcess> sagaProcesses;
    private Set<StateStepProcess> stateStepProcesses;

}
