package uk.co.n3fs.sasha.database.migration;

import co.aikar.idb.Database;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import uk.co.n3fs.sasha.database.DatabaseManager;

public class Migrations {

    private static Map<Integer, Migration> migrations = new HashMap<>();

    static {
        migrations.put(0, Migrations::setupTables);
    }

    public static void performMigrations(final DatabaseManager manager) throws MigrationException {
        final Database db = manager.getDatabase();
        boolean pendingMigration = true;

        try {
            while (pendingMigration) {
                pendingMigration = false;
                int version = db.getFirstColumn(manager.replace("SELECT VERSION FROM {prefix}meta"));
                if (migrations.containsKey(version)) {
                    manager.getLogger().info("Performing migration from data version " + version);
                    migrations.get(version).run(manager);
                    pendingMigration = true;
                }
            }
        } catch (SQLException e) {
            // SQLException inside the migrations themselves should already be caught, so this should only ever get
            // thrown if getting the data version fails.
            throw new MigrationException("Failed to retrieve current data version!", e);
        }
    }

    private static void setupTables(DatabaseManager manager) throws MigrationException {
        try {
            manager.runDefinedQuery("migration_0");
        } catch (SQLException e) {
            throw new MigrationException("Failed to run query!", e);
        }
    }
}
