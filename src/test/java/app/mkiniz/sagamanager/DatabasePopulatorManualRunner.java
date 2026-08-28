package app.mkiniz.sagamanager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@SpringBootTest
@Disabled("This test should not be executed with the standard test suite. Run manually to populate the database.")
class DatabasePopulatorManualRunner {

    @Autowired
    private DataSource dataSource;

    @Test
    void populateDatabase() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("scripts/clear_db.sql"));
        populator.addScript(new ClassPathResource("scripts/populate_db.sql"));
        populator.execute(dataSource);
        System.out.println("Database populated successfully with scripts/populate_db.sql");
    }
}
