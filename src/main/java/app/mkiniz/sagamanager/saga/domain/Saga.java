package app.mkiniz.sagamanager.saga.domain;

import app.mkiniz.sagamanager.shared.entity.AbstractAuditable;
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
public class Saga extends AbstractAuditable {
    private long id;
    private String name;
    private String description;
    private boolean deleted;
}
