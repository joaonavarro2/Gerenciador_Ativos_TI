package br.com.stecar.stecar_backend;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ActiveProfiles("test")
abstract class PostgresIntegrationTest {

    protected static final String TEST_DATABASE_URL = "jdbc:postgresql://localhost:5432/stecar_test";
    protected static final String TEST_DATABASE_USERNAME =
            System.getenv().getOrDefault("STECAR_TEST_DB_USER", "postgres");
    protected static final String TEST_DATABASE_PASSWORD = requiredTestDatabasePassword();

    @DynamicPropertySource
    static void configurarPostgresLocalIsolado(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> TEST_DATABASE_URL);
        registry.add("spring.datasource.username", () -> TEST_DATABASE_USERNAME);
        registry.add("spring.datasource.password", () -> TEST_DATABASE_PASSWORD);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.url", () -> TEST_DATABASE_URL);
        registry.add("spring.flyway.user", () -> TEST_DATABASE_USERNAME);
        registry.add("spring.flyway.password", () -> TEST_DATABASE_PASSWORD);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    private static String requiredTestDatabasePassword() {
        String password = System.getenv("STECAR_TEST_DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Defina STECAR_TEST_DB_PASSWORD para conectar somente ao banco local stecar_test.");
        }
        return password;
    }
}
