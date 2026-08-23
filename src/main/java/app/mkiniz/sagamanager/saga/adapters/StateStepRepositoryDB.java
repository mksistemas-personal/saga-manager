package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.StateStep;
import app.mkiniz.sagamanager.saga.domain.StateStepRepository;
import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
class StateStepRepositoryDB implements StateStepRepository {

    private static final String EVENTS_SEPARATOR = "|";

    private final JdbcClient jdbcClient;

    @Override
    public StateStep save(StateStep stateStep) {
        if (Objects.isNull(stateStep.getId())) {
            stateStep.setId(TsidCreator.getTsid().toString());
        }

        String sql = """
                INSERT INTO state_step (id, name, description, events, created_at, updated_at, created_by, updated_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    name = EXCLUDED.name,
                    description = EXCLUDED.description,
                    events = EXCLUDED.events,
                    updated_at = EXCLUDED.updated_at,
                    updated_by = EXCLUDED.updated_by
                """;

        long idAsLong = Tsid.from(stateStep.getId()).toLong();

        jdbcClient.sql(sql)
                .param(idAsLong)
                .param(stateStep.getName())
                .param(stateStep.getDescription())
                .param(String.join(EVENTS_SEPARATOR, stateStep.getEvents()))
                .param(toOffsetDateTime(stateStep.getCreatedAt()))
                .param(toOffsetDateTime(stateStep.getUpdatedAt()))
                .param(stateStep.getCreatedBy())
                .param(stateStep.getUpdatedBy())
                .update();

        return stateStep;
    }

    private static OffsetDateTime toOffsetDateTime(ZonedDateTime zonedDateTime) {
        return Objects.isNull(zonedDateTime) ? null : zonedDateTime.toOffsetDateTime();
    }
}
