package uk.co.n3fs.sasha.database;

import co.aikar.idb.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import uk.co.n3fs.sasha.SashaPlugin;
import uk.co.n3fs.sasha.database.migration.Migrations;

public class DatabaseManager {

    private final SashaPlugin plugin;
    private final String path;
    private Database db;
    private boolean available = false;

    public DatabaseManager(SashaPlugin plugin) {
        this.plugin = plugin;
        this.path = plugin.getPluginDir().resolve("data").toAbsolutePath().toString();
    }

    public void setupDatabase() throws Exception {
        DatabaseOptions options = DatabaseOptions.builder()
            .driverClassName("org.h2.Driver")
            .dataSourceClassName("org.h2.jdbcx.JdbcDataSource")
            .dsn("h2:file:" + path)
            .poolName("sasha-pool")
            .useOptimizations(false)
            .build();

        db = PooledDatabaseOptions.builder().options(options).createHikariDatabase();

        db.executeUpdate(replace("CREATE TABLE IF NOT EXISTS {prefix}meta (VERSION INT)"));
        List<Integer> versions = db.getFirstColumnResults(replace("SELECT VERSION FROM {prefix}meta"));
        if (versions.size() == 0) {
            db.executeUpdate(replace("INSERT INTO {prefix}meta (VERSION) VALUES (0)"));
        }
        Migrations.performMigrations(this);
        available = true;
    }

    public String replace(String query) {
        return query.replace("{prefix}", "sasha_"); // TODO: config (?)
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public Database getDatabase() {
        return db;
    }

    public boolean isAvailable() {
        return available;
    }

    /**
     * Given a name, reads the contents of /schemas/(name).sql from the jar and replace any tokens.
     * @param name The name of the query
     * @return The statement, or null if it cannot be loaded.
     */
    public String getDefinedQuery(final String name) {
        try {
            return replace(SashaPlugin.getResource("/schemas/" + name + ".sql"));
        } catch (IOException e) {
            plugin.getLogger().error("Failed to load schema " + name, e);
            return null;
        }
    }

    public void runDefinedQuery(final String queryName) throws SQLException {
        final String query = getDefinedQuery(queryName);
        if (query == null) throw new RuntimeException("Failed to load defined query " + queryName + "!");

        db.executeUpdate(query);
    }

    public void close() {
        available = false;
        db.close();
    }
}
