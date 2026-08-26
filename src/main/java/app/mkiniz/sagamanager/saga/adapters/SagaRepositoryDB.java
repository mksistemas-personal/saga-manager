package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
import app.mkiniz.sagamanager.saga.domain.SagaSearchRequest;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class SagaRepositoryDB implements SagaRepository {

    private static final String SQL_SAGA
            = """
            SELECT id, name, description, deleted, created_at, updated_at, created_by, updated_by
            FROM saga
            WHERE deleted = false
            """;

    private final JdbcClient jdbcClient;

    @Override
    public Saga save(Saga saga) {
        if (Objects.isNull(saga.getId()))
            saga.setId(TsidCreator.getTsid());

        String sql = """
                INSERT INTO saga (id, name, description, deleted, created_at, updated_at, created_by, updated_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    name = EXCLUDED.name,
                    description = EXCLUDED.description,
                    deleted = EXCLUDED.deleted,
                    updated_at = EXCLUDED.updated_at,
                    updated_by = EXCLUDED.updated_by
                """;

        jdbcClient.sql(sql)
                .param(saga.getId().toLong())
                .param(saga.getName())
                .param(saga.getDescription())
                .param(saga.isDeleted())
                .param(toOffsetDateTime(saga.getCreatedAt()))
                .param(toOffsetDateTime(saga.getUpdatedAt()))
                .param(saga.getCreatedBy())
                .param(saga.getUpdatedBy())
                .update();

        return saga;
    }

    @Override
    public Optional<Saga> findById(Tsid id) {
        String sql = SQL_SAGA + " and id = :id";
        return jdbcClient.sql(sql)
                .param("id", id.toLong())
                .query((rs, rowNum) -> translateSagaFromQuery(rs))
                .optional();
    }

    private static Saga translateSagaFromQuery(ResultSet rs) throws SQLException {
        return Saga.builder()
                .id(Tsid.from(rs.getLong("id")))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .deleted(rs.getBoolean("deleted"))
                .createdAt(toZonedDateTime(rs.getObject("created_at", OffsetDateTime.class)))
                .updatedAt(toZonedDateTime(rs.getObject("updated_at", OffsetDateTime.class)))
                .createdBy(rs.getString("created_by"))
                .updatedBy(rs.getString("updated_by"))
                .build();
    }

    @Override
    public Slice<Saga> findBySearchRequest(SagaSearchRequest request, Pageable pageable) {
        StringBuilder sqlBuilder = new StringBuilder(SQL_SAGA);
        boolean hasName = Objects.nonNull(request) && Objects.nonNull(request.name());
        if (hasName) {
            sqlBuilder.append(" AND name ILIKE :name ");
        }
        sqlBuilder.append(" LIMIT :limit OFFSET :offset");
        JdbcClient.StatementSpec queryData = jdbcClient.sql(sqlBuilder.toString());
        if (hasName) {
            queryData.param("name", "%" + request.name() + "%");
        }
        queryData.param("limit", pageable.getPageSize() + 1);
        queryData.param("offset", pageable.getOffset());
        List<Saga> elements = queryData
                .query((rs, rowNum) -> translateSagaFromQuery(rs))
                .list();

        boolean hasNext = elements.size() > pageable.getPageSize();
        if (hasNext) {
            elements = elements.subList(0, pageable.getPageSize());
        }
        return new SliceImpl<>(elements, pageable, hasNext);
    }

    @Override
    public Tsid saveLinkRelationWithStateStep(Tsid sagaId, Tsid stateStepId) {
        String sql = """
                WITH old AS (
                    SELECT id, step_id AS old_step_id
                    FROM saga_step_relationship
                    WHERE saga_id = :sagaId
                ),
                updated AS (
                    UPDATE saga_step_relationship r
                    SET step_id = :stepId, is_valid = true
                    FROM old
                    WHERE r.id = old.id
                    RETURNING r.id
                ),
                inserted AS (
                    INSERT INTO saga_step_relationship (id, saga_id, step_id, is_valid)
                    SELECT :newId, :sagaId, :stepId, true
                    WHERE NOT EXISTS (SELECT 1 FROM old)
                    RETURNING step_id
                )
                SELECT old.old_step_id FROM old
                UNION ALL
                SELECT step_id FROM inserted;
                """;

        Long returnedId = jdbcClient.sql(sql)
                .param("newId", TsidCreator.getTsid().toLong())
                .param("sagaId", sagaId.toLong())
                .param("stepId", stateStepId.toLong())
                .query(Long.class)
                .single();

        return Tsid.from(returnedId);
    }

    private static OffsetDateTime toOffsetDateTime(ZonedDateTime zonedDateTime) {
        return Objects.isNull(zonedDateTime) ? null : zonedDateTime.toOffsetDateTime();
    }

    private static ZonedDateTime toZonedDateTime(OffsetDateTime offsetDateTime) {
        return Objects.isNull(offsetDateTime) ? null : offsetDateTime.toZonedDateTime();
    }
}
