package app.mkiniz.sagamanager.shared.entity;

import app.mkiniz.sagamanager.shared.security.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractAuditable {

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public void fillToCreate() {
        String currentUser = SecurityUtils.getCurrentUserLogin().orElse("system");
        createdAt = ZonedDateTime.now();
        createdBy = currentUser;
        updatedAt = ZonedDateTime.now();
        updatedBy = currentUser;
    }

    public void fillToUpdate() {
        String currentUser = SecurityUtils.getCurrentUserLogin().orElse("system");
        updatedAt = ZonedDateTime.now();
        updatedBy = currentUser;
    }


}
