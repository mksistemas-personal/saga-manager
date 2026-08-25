package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.*;
import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class StateStepRepositoryDB implements StateStepRepository {

    private static final String EVENTS_SEPARATOR = "\\|";
    private static final String EVENTS_JOIN_SEPARATOR = "|";

    private static final String SQL_STATE_STEP = """
            SELECT id, name, description, is_composite, deleted, events, created_at, updated_at, created_by, updated_by
            FROM state_step
            WHERE deleted = false
            """;

    private final JdbcClient jdbcClient;

    @Override
    public StateStep save(StateStep stateStep) {
        if (Objects.isNull(stateStep.getId())) {
            stateStep.setId(TsidCreator.getTsid().toString());
        }

        String sql = """
                INSERT INTO state_step (id, name, description, events, deleted, created_at, updated_at, created_by, updated_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    name = EXCLUDED.name,
                    description = EXCLUDED.description,
                    events = EXCLUDED.events,
                    deleted = EXCLUDED.deleted,
                    updated_at = EXCLUDED.updated_at,
                    updated_by = EXCLUDED.updated_by
                """;

        long idAsLong = Tsid.from(stateStep.getId()).toLong();

        String eventsStr = null;
        if (Objects.nonNull(stateStep.getEvents())) {
            eventsStr = String.join(EVENTS_JOIN_SEPARATOR, stateStep.getEvents());
        }

        jdbcClient.sql(sql)
                .param(idAsLong)
                .param(stateStep.getName())
                .param(stateStep.getDescription())
                .param(eventsStr)
                .param(stateStep.isDeleted())
                .param(toOffsetDateTime(stateStep.getCreatedAt()))
                .param(toOffsetDateTime(stateStep.getUpdatedAt()))
                .param(stateStep.getCreatedBy())
                .param(stateStep.getUpdatedBy())
                .update();

        return stateStep;
    }

    @Override
    public Optional<StateStep> findById(Tsid id) {
        String sql = SQL_STATE_STEP + " and id = :id";
        return jdbcClient.sql(sql)
                .param("id", id.toLong())
                .query((rs, rowNum) -> translateStateStepFromQuery(rs))
                .optional();
    }

    private static StateStep translateStateStepFromQuery(ResultSet rs) throws SQLException {
        boolean isComposite = rs.getBoolean("is_composite");
        String eventsStr = rs.getString("events");
        java.util.List<String> events = null;
        if (eventsStr != null && !eventsStr.isEmpty()) {
            events = Arrays.asList(eventsStr.split(EVENTS_SEPARATOR));
        }

        StateStep.StateStepBuilder<?, ?> builder = isComposite ? CompositeStateStep.builder() : SingleStateStep.builder();

        return builder
                .id(Tsid.from(rs.getLong("id")).toString())
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .deleted(rs.getBoolean("deleted"))
                .events(events)
                .createdAt(toZonedDateTime(rs.getObject("created_at", OffsetDateTime.class)))
                .updatedAt(toZonedDateTime(rs.getObject("updated_at", OffsetDateTime.class)))
                .createdBy(rs.getString("created_by"))
                .updatedBy(rs.getString("updated_by"))
                .build();
    }

    @Override
    public Slice<StateStep> findBySearchRequest(StateStepSearchRequest request, Pageable pageable) {
        StringBuilder sqlBuilder = new StringBuilder(SQL_STATE_STEP);
        boolean hasName = Objects.nonNull(request) && Objects.nonNull(request.name());
        boolean hasEvent = Objects.nonNull(request) && Objects.nonNull(request.event());

        if (hasName) {
            sqlBuilder.append(" AND name ILIKE :name ");
        }
        if (hasEvent) {
            sqlBuilder.append(" AND events ILIKE :event ");
        }

        sqlBuilder.append(" LIMIT :limit OFFSET :offset");

        JdbcClient.StatementSpec queryData = jdbcClient.sql(sqlBuilder.toString());

        if (hasName) {
            queryData.param("name", "%" + request.name() + "%");
        }
        if (hasEvent) {
            queryData.param("event", "%" + request.event() + "%");
        }

        queryData.param("limit", pageable.getPageSize() + 1);
        queryData.param("offset", pageable.getOffset());

        List<StateStep> elements = queryData
                .query((rs, rowNum) -> translateStateStepFromQuery(rs))
                .list();

        boolean hasNext = elements.size() > pageable.getPageSize();
        if (hasNext) {
            elements = elements.subList(0, pageable.getPageSize());
        }
        return new SliceImpl<>(elements, pageable, hasNext);
    }

    private static OffsetDateTime toOffsetDateTime(ZonedDateTime zonedDateTime) {
        return Objects.isNull(zonedDateTime) ? null : zonedDateTime.toOffsetDateTime();
    }

    private static ZonedDateTime toZonedDateTime(OffsetDateTime offsetDateTime) {
        return Objects.isNull(offsetDateTime) ? null : offsetDateTime.toZonedDateTime();
    }
}
