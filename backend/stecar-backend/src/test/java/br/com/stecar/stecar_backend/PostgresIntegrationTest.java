package br.com.stecar.stecar_backend;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ActiveProfiles("test")
abstract class PostgresIntegrationTest {

    static {
        requireDockerIntegrationRunner();
    }

    protected static final String TEST_DATABASE_HOST =
            requiredSetting("STECAR_TEST_DB_HOST", "database");
    private static final String TEST_DATABASE_PORT =
            requiredSetting("STECAR_TEST_DB_PORT", "5432");
    private static final String TEST_DATABASE_NAME =
            requiredSetting("STECAR_TEST_DB_NAME", "stecar_integration");
    protected static final String TEST_DATABASE_USERNAME =
            requiredSetting("STECAR_TEST_DB_USER", "stecar_it");
    protected static final String TEST_DATABASE_PASSWORD = requiredTestDatabasePassword();
    protected static final String TEST_DATABASE_URL =
            "jdbc:postgresql://" + TEST_DATABASE_HOST + ":" + TEST_DATABASE_PORT + "/" + TEST_DATABASE_NAME;

    @DynamicPropertySource
    static void configurarPostgresDescartavel(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> TEST_DATABASE_URL);
        registry.add("spring.datasource.username", () -> TEST_DATABASE_USERNAME);
        registry.add("spring.datasource.password", () -> TEST_DATABASE_PASSWORD);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.url", () -> TEST_DATABASE_URL);
        registry.add("spring.flyway.user", () -> TEST_DATABASE_USERNAME);
        registry.add("spring.flyway.password", () -> TEST_DATABASE_PASSWORD);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.schemas", () -> "public");
        registry.add("spring.flyway.default-schema", () -> "public");
        registry.add("spring.flyway.clean-disabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    private static void requireDockerIntegrationRunner() {
        if (!"1".equals(System.getenv("STECAR_INTEGRATION_RUNNER"))
                || !Files.isRegularFile(Path.of("/.dockerenv"))) {
            throw new IllegalStateException(
                    "Testes de integração PostgreSQL devem ser executados pelo runner Docker isolado.");
        }
    }

    private static String requiredSetting(String name, String expected) {
        String value = System.getenv(name);
        if (!expected.equals(value)) {
            throw new IllegalStateException(
                    name + " deve estar definido para o valor do banco PostgreSQL descartável.");
        }
        return value;
    }

    private static String requiredTestDatabasePassword() {
        String password = System.getenv("STECAR_TEST_DB_PASSWORD");
        if (password == null || password.length() < 32) {
            throw new IllegalStateException(
                    "STECAR_TEST_DB_PASSWORD deve conter ao menos 32 caracteres.");
        }
        return password;
    }
}
