package app.mkiniz.sagamanager.saga.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompositeStateStep extends StateStep {

    @JsonProperty("composite")
    private boolean composite() {
        return true;
    }

    @JsonIgnore
    private List<StateStep> steps;

    @JsonProperty("steps")
    public List<String> getStepsIds() {
        if (this.steps == null) {
            return null;
        }
        return this.steps.stream()
                .map(StateStep::getId)
                .collect(Collectors.toList());
    }
}
