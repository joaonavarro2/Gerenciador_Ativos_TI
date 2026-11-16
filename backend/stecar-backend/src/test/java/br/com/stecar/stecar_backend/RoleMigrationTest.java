package br.com.stecar.stecar_backend;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;

class RoleMigrationTest extends PostgresIntegrationTest {

    @Test
    void migrationPromoveUsuariosLegadosPreservaStatusERejeitaFuncaoAntiga() throws SQLException {
        String schema = "role_migration_" + UUID.randomUUID().toString().replace("-", "");
        try {
            Flyway.configure()
                    .dataSource(TEST_DATABASE_URL, TEST_DATABASE_USERNAME, TEST_DATABASE_PASSWORD)
                    .schemas(schema)
                    .defaultSchema(schema)
                    .locations("classpath:db/migration")
                    .target(MigrationVersion.fromVersion("2"))
                    .load()
                    .migrate();

            try (Connection connection = DriverManager.getConnection(
                    TEST_DATABASE_URL, TEST_DATABASE_USERNAME, TEST_DATABASE_PASSWORD);
                    var statement = connection.createStatement()) {
                statement.executeUpdate("INSERT INTO " + schema
                        + ".usuario (nome_completo, email, senha, funcao, status) VALUES "
                        + "('Legado ativo', 'legado-ativo@teste.local', 'hash', 'USUARIO', 'ATIVO'), "
                        + "('Legado inativo', 'legado-inativo@teste.local', 'hash', 'USUARIO', 'INATIVO')");
            }

            Flyway.configure()
                    .dataSource(TEST_DATABASE_URL, TEST_DATABASE_USERNAME, TEST_DATABASE_PASSWORD)
                    .schemas(schema)
                    .defaultSchema(schema)
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();

            try (Connection connection = DriverManager.getConnection(
                    TEST_DATABASE_URL, TEST_DATABASE_USERNAME, TEST_DATABASE_PASSWORD);
                    var statement = connection.createStatement();
                    var rows = statement.executeQuery(
                            "SELECT funcao, status FROM " + schema + ".usuario ORDER BY status")) {
                assertTrue(rows.next());
                assertEquals("ADMINISTRADOR", rows.getString("funcao"));
                assertEquals("ATIVO", rows.getString("status"));
                assertTrue(rows.next());
                assertEquals("ADMINISTRADOR", rows.getString("funcao"));
                assertEquals("INATIVO", rows.getString("status"));
                assertFalse(rows.next());
                assertThrows(SQLException.class, () -> statement.executeUpdate(
                        "INSERT INTO " + schema
                                + ".usuario (nome_completo, email, senha, funcao, status) "
                                + "VALUES ('Legado inválido', 'legado-invalido@teste.local', "
                                + "'hash', 'USUARIO', 'ATIVO')"));
            }
        } finally {
            try (Connection connection = DriverManager.getConnection(
                    TEST_DATABASE_URL, TEST_DATABASE_USERNAME, TEST_DATABASE_PASSWORD);
                    var statement = connection.createStatement()) {
                statement.execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
            }
        }
    }
}
