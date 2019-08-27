package uk.co.n3fs.sasha.database.migration;

import uk.co.n3fs.sasha.database.DatabaseManager;

@FunctionalInterface
public interface Migration {

    void run(DatabaseManager manager) throws MigrationException;

}
