package app.mkiniz.sagamanager.adapters;

import app.mkiniz.sagamanager.saga.domain.Saga;
import app.mkiniz.sagamanager.saga.domain.SagaRepository;
import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class SagaRepositoryDB implements SagaRepository {

    private final JdbcTemplate jdbcTemplate;

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

        jdbcTemplate.update(sql,
                saga.getId().toLong(),
                saga.getName(),
                saga.getDescription(),
                saga.isDeleted(),
                toOffsetDateTime(saga.getCreatedAt()),
                toOffsetDateTime(saga.getUpdatedAt()),
                saga.getCreatedBy(),
                saga.getUpdatedBy()
        );

        return saga;
    }

    @Override
    public Optional<Saga> findById(Tsid id) {
        String sql = """
                SELECT id, name, description, deleted, created_at, updated_at, created_by, updated_by
                FROM saga
                WHERE id = ? and deleted = false
                """;

        return jdbcTemplate.<Saga>query(sql, (rs, rowNum) -> Saga.builder()
                .id(Tsid.from(rs.getLong("id")))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .deleted(rs.getBoolean("deleted"))
                .createdAt(toZonedDateTime(rs.getObject("created_at", OffsetDateTime.class)))
                .updatedAt(toZonedDateTime(rs.getObject("updated_at", OffsetDateTime.class)))
                .createdBy(rs.getString("created_by"))
                .updatedBy(rs.getString("updated_by"))
                .build(), id.toLong()).stream().findFirst();
    }

    private static OffsetDateTime toOffsetDateTime(ZonedDateTime zonedDateTime) {
        return Objects.isNull(zonedDateTime) ? null : zonedDateTime.toOffsetDateTime();
    }

    private static ZonedDateTime toZonedDateTime(OffsetDateTime offsetDateTime) {
        return Objects.isNull(offsetDateTime) ? null : offsetDateTime.toZonedDateTime();
    }
}
