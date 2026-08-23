package app.mkiniz.sagamanager.saga.domain;

import app.mkiniz.sagamanager.shared.entity.AbstractAuditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StateStep extends AbstractAuditable {
    private String id;
    private String name;
    private String description;
    private List<String> events;
    private List<StateStep> connections;
}
