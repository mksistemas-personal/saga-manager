package app.mkiniz.sagamanager.process.domain;

import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StateStepProcess {
    private Tsid id;
    private String name;
    private Set<String> events;
    private Map<String, StateStepProcess> origins;
}
