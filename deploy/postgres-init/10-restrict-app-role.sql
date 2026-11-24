-- The official image initially creates POSTGRES_USER as a bootstrap superuser.
-- The application needs database ownership for Flyway, but not cluster privileges.
ALTER ROLE stecar_app
    WITH NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
