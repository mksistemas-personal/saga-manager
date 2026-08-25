package app.mkiniz.sagamanager.saga.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
public class SingleStateStep extends StateStep {
    @JsonProperty("single")
    public boolean single() {
        return true;
    }

}
