package uk.co.n3fs.sasha.ticket;

import java.sql.SQLException;
import java.time.Instant;
import uk.co.n3fs.sasha.UserCache;
import uk.co.n3fs.sasha.database.DatabaseManager;

public class TicketManager {

    private final DatabaseManager dbManager;

    public TicketManager(DatabaseManager dbManager, UserCache userCache) {
        this.dbManager = dbManager;
    }

    // TODO: creation, updates, messages, assigning, subscriptions
    // TODO later: companion plugin

    public Ticket createTicket(Ticket ticket) {
        if (ticket.getId() != null) throw new RuntimeException("Cannot create ticket that already has an ID.");

        String query = dbManager.replace("INSERT INTO {prefix}tickets ( reporter_id, reported_at, updated_at, open, location_x, location_y, location_z, location_world, assignee_id ) VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? )");

        String reporter = ticket.getReporter().toString();
        Instant reportedAt = ticket.getReportedAt();
        Instant updatedAt = ticket.getUpdatedAt();
        boolean open = ticket.isOpen();
        String assignee = ticket.getAssignee() != null ? ticket.getAssignee().toString() : null;
        Location location = ticket.getLocation();
        String world = location != null ? location.getWorld() : null;
        Integer x = location != null ? location.getX() : null;
        Integer y = location != null ? location.getY() : null;
        Integer z = location != null ? location.getZ() : null;

        try {
            long id = dbManager.getDatabase().executeInsert(query, reporter, reportedAt, updatedAt, open, x, y, z, world, assignee);
            return Ticket.Builder.from(ticket).setId((int) id).build();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting ticket into database!", e);
        }
    }
}
