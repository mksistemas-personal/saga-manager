package app.mkiniz.sagamanager.saga.adapters;

import app.mkiniz.sagamanager.saga.adapters.SagaRepositoryDB;
import app.mkiniz.sagamanager.saga.domain.Saga;
import com.github.f4b6a3.tsid.TsidCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@Import(SagaRepositoryDB.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SagaRepositoryDBTest {

    @Autowired
    private SagaRepositoryDB sagaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSaveSaga() {
        Saga saga = Saga.builder()
                .id(TsidCreator.getTsid())
                .name("Test Saga")
                .description("Test Description")
                .deleted(false)
                .createdAt(ZonedDateTime.now())
                .createdBy("test-user")
                .updatedAt(ZonedDateTime.now())
                .updatedBy("test-user")
                .build();

        sagaRepository.save(saga);

        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * FROM saga WHERE id = ?", saga.getId().toLong());

        assertNotNull(result);
        assertEquals(saga.getId().toLong(), ((Number) result.get("id")).longValue());
        assertEquals("Test Saga", result.get("name"));
        assertEquals("Test Description", result.get("description"));
        assertEquals(false, result.get("deleted"));
        assertEquals("test-user", result.get("created_by"));
    }
}
