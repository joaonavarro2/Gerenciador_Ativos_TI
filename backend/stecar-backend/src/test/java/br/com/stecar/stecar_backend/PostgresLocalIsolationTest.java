package br.com.stecar.stecar_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class PostgresLocalIsolationTest extends PostgresIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Flyway flyway;

    @Test
    void datasourceUsaPostgresLocalIsoladoEExecutaTodasAsMigrations() throws SQLException {
        try (Connection connection = dataSource.getConnection();
                var statement = connection.createStatement();
                var result = statement.executeQuery("""
                        SELECT current_database(), current_user, inet_server_port(), version()
                        """)) {
            assertEquals(TEST_DATABASE_URL, connection.getMetaData().getURL());
            assertEquals("stecar_test", result.next() ? result.getString(1) : null);
            assertEquals(TEST_DATABASE_USERNAME, result.getString(2));
            assertEquals(5432, result.getInt(3));
            assertTrue(result.getString(4).startsWith("PostgreSQL"),
                    "A conexão deve chegar ao PostgreSQL instalado localmente.");
        }

        assertEquals(17, flyway.info().applied().length,
                "Todas as 17 migrations Flyway devem estar aplicadas no banco de teste.");
        assertEquals("17", flyway.info().current().getVersion().getVersion());
    }
}
