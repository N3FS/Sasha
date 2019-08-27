package uk.co.n3fs.sasha.database.migration;

public class MigrationException extends Exception {

    private final String message;
    private final Exception exception;

    MigrationException(final String message) {
        this.message = message;
        this.exception = null;
    }

    MigrationException(final String message, final Exception exception) {
        this.message = message;
        this.exception = exception;
    }

}
