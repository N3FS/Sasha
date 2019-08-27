package uk.co.n3fs.sasha.ticket;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
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

        String query = dbManager.replace("INSERT INTO {prefix}tickets ( reporter_id, reported_at, updated_at, open, location_x, location_y, location_z, location_world, assignee_id ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )");

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
            throw new RuntimeException("Error inserting ticket into database", e);
        }
    }

    public TicketComment createTicketComment(TicketComment comment) {
        if (comment.getId() != null) throw new RuntimeException("Cannot create ticket that already has an ID.");

        String conversationIdsQuery = dbManager.replace("SELECT conversation_id FROM {prefix}ticket_comments WHERE ticket_id = ?");
        String insertCommentQuery = dbManager.replace("INSERT INTO {prefix}ticket_comments ( ticket_id, author_id, conversation_id, written_at, message, new_open_state ) VALUES ( ?, ?, ?, ?, ?, ? )");
        String updateTicketQuery = dbManager.replace("UPDATE {prefix}ticket_comments SET updated_at = ? WHERE id = ?");

        int ticketId = comment.getTicket();
        String authorId = comment.getAuthor().toString();
        Instant writtenAt = comment.getWrittenAt();
        String message = comment.getMessage();
        Boolean newState = comment.getNewState();

        final int[] commentId = new int[1];

        boolean success = dbManager.getDatabase().createTransaction(stm -> {
            List<Integer> conversationIds = stm.executeQueryGetFirstColumnResults(conversationIdsQuery, ticketId);
            int nextConversationId = Collections.max(conversationIds) + 1;

            int rows;
            rows = stm.executeUpdateQuery(insertCommentQuery, ticketId, authorId, nextConversationId, writtenAt, message, newState);
            if (rows < 1) {
                return false;
            }
            commentId[0] = stm.getLastInsertId().intValue();

            rows = stm.executeUpdateQuery(updateTicketQuery, writtenAt, ticketId);
            return rows == 1;
        });
        
        return success ? TicketComment.Builder.from(comment).setId(commentId[0]).build() : null;
    }
}
