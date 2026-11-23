package br.com.stecar.stecar_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.sql.Connection;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PostgresIntegrationIsolationTest extends PostgresIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Flyway flyway;

    @Test
    void datasourceUsaPostgresDescartavelEExecutaTodasAsMigrations() throws Exception {
        assertEquals("database", TEST_DATABASE_HOST);
        assertFalse(InetAddress.getByName(TEST_DATABASE_HOST).isLoopbackAddress());

        try (Connection connection = dataSource.getConnection();
                var statement = connection.createStatement();
                var result = statement.executeQuery("""
                        SELECT current_database(), current_user, inet_server_port(), version()
                        """)) {
            assertEquals(TEST_DATABASE_URL, connection.getMetaData().getURL());
            assertEquals("stecar_integration", result.next() ? result.getString(1) : null);
            assertEquals(TEST_DATABASE_USERNAME, result.getString(2));
            assertEquals("stecar_it", TEST_DATABASE_USERNAME);
            assertEquals(5432, result.getInt(3));
            assertTrue(result.getString(4).startsWith("PostgreSQL"),
                    "A conexão deve chegar ao PostgreSQL descartável na rede Docker.");
        }

        assertTrue(flyway.info().applied().length > 0,
                "O banco descartável deve ter migrations aplicadas.");
        assertEquals(0, flyway.info().pending().length,
                "O banco descartável não deve ter migrations pendentes.");
    }
}
