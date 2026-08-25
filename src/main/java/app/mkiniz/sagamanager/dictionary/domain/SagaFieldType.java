package app.mkiniz.sagamanager.dictionary.domain;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SagaFieldType {
    private Long id;
    private String name;
    private SagaTypeEnum sagaType;
}
